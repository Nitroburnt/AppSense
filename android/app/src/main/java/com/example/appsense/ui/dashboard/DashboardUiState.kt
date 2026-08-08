package com.example.appsense.ui.dashboard

import com.example.appsense.domain.model.AppCategory
import com.example.appsense.domain.model.AppInfo
import com.example.appsense.domain.model.AppSummary
import com.example.appsense.domain.model.SortOrder
import com.example.appsense.domain.model.UsageData

data class DashboardUiState(
    val apps: List<AppInfo> = emptyList(),
    val activeCategory: AppCategory = AppCategory.ALL,
    val sortOrder: SortOrder = SortOrder.NAME_ASC,
    val isLoading: Boolean = false,
    val error: String? = null,
    val usageStats: Map<String, UsageData> = emptyMap(),
    val hasUsagePermission: Boolean = true,
    val summaries: Map<String, AppSummary> = emptyMap(),
    val loadingSummaries: Set<String> = emptySet()
)
