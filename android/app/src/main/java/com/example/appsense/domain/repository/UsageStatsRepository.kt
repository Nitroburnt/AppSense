package com.example.appsense.domain.repository

import com.example.appsense.domain.model.UsageData

interface UsageStatsRepository {
    fun getUsageStats(): Map<String, UsageData>
    fun hasPermission(): Boolean
}
