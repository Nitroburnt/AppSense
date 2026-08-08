package com.example.appsense.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appsense.data.source.local.InstallSourceChecker
import com.example.appsense.domain.model.AppCategory
import com.example.appsense.domain.model.AppInfo
import com.example.appsense.domain.model.SortOrder
import com.example.appsense.domain.repository.AppSummaryRepository
import com.example.appsense.domain.repository.PackageRepository
import com.example.appsense.domain.repository.UsageStatsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val packageRepository: PackageRepository,
    private val usageStatsRepository: UsageStatsRepository,
    private val installSourceChecker: InstallSourceChecker,
    private val appSummaryRepository: AppSummaryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    val visibleApps: List<AppInfo>
        get() {
            val state = _uiState.value
            val filtered = when (state.activeCategory) {
                AppCategory.ALL -> state.apps
                AppCategory.UNUSED -> state.apps.filter {
                    (state.usageStats[it.packageName]?.totalTimeInForeground ?: 0L) == 0L
                }
                AppCategory.MOST_USED -> state.apps.filter {
                    (state.usageStats[it.packageName]?.totalTimeInForeground ?: 0L) > 0L
                }
                AppCategory.SIDELOADED -> state.apps.filter {
                    installSourceChecker.isSideloaded(it.installSource)
                }
            }
            val usage = state.usageStats
            return when (state.sortOrder) {
                SortOrder.NAME_ASC -> filtered.sortedBy { it.appName.lowercase() }
                SortOrder.NAME_DESC -> filtered.sortedByDescending { it.appName.lowercase() }
                SortOrder.USAGE_HIGH -> filtered.sortedByDescending { usage[it.packageName]?.totalTimeInForeground ?: 0L }
                SortOrder.USAGE_LOW -> filtered.sortedBy { usage[it.packageName]?.totalTimeInForeground ?: 0L }
                SortOrder.INSTALL_NEW -> filtered.sortedByDescending { it.installDate }
                SortOrder.INSTALL_OLD -> filtered.sortedBy { it.installDate }
                SortOrder.SIZE_LARGE -> filtered.sortedByDescending { it.appSizeBytes }
                SortOrder.SIZE_SMALL -> filtered.sortedBy { it.appSizeBytes }
            }
        }

    init {
        viewModelScope.launch {
            try {
                packageRepository.getInstalledApps().collect { apps ->
                    _uiState.update {
                        it.copy(apps = apps, isLoading = false, error = null)
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
        refreshUsage()
    }

    fun refreshUsage() {
        viewModelScope.launch {
            val hasPermission = usageStatsRepository.hasPermission()
            val usage = if (hasPermission) usageStatsRepository.getUsageStats() else emptyMap()
            _uiState.update {
                it.copy(usageStats = usage, hasUsagePermission = hasPermission, isLoading = it.apps.isEmpty())
            }
        }
    }

    fun selectCategory(category: AppCategory) {
        _uiState.update { it.copy(activeCategory = category) }
    }

    fun selectSortOrder(sortOrder: SortOrder) {
        _uiState.update { it.copy(sortOrder = sortOrder) }
    }

    fun onAppExpanded(app: AppInfo) {
        val state = _uiState.value
        if (app.packageName in state.summaries || app.packageName in state.loadingSummaries) return
        _uiState.update { it.copy(loadingSummaries = it.loadingSummaries + app.packageName) }
        viewModelScope.launch {
            appSummaryRepository.getSummary(app.packageName, app.appName)
                .first()
                .onSuccess { summary ->
                    _uiState.update {
                        it.copy(
                            summaries = it.summaries + (app.packageName to summary),
                            loadingSummaries = it.loadingSummaries - app.packageName
                        )
                    }
                }
                .onFailure {
                    _uiState.update { it.copy(loadingSummaries = it.loadingSummaries - app.packageName) }
                }
        }
    }
}
