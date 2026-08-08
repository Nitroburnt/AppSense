package com.example.appsense.ui.onboarding

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.appsense.domain.permission.AppPermissionGuard
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class OnboardingUiState(
    val termsAccepted: Boolean = false,
    val permissionRequested: Boolean = false,
    val onboardingComplete: Boolean = false
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val guard: AppPermissionGuard
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun acceptTerms() {
        _uiState.update { it.copy(termsAccepted = true) }
    }

    fun onPermissionRequested() {
        _uiState.update { it.copy(permissionRequested = true) }
    }

    fun onResume() {
        if (guard.hasUsageStatsPermission(context)) {
            guard.setOnboardingComplete(context)
            _uiState.update { it.copy(onboardingComplete = true) }
        }
    }
}
