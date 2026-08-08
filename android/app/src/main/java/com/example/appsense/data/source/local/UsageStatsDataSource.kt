package com.example.appsense.data.source.local

import android.app.usage.UsageStatsManager
import android.content.Context
import com.example.appsense.domain.model.UsageData
import com.example.appsense.domain.permission.AppPermissionGuard
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class UsageStatsDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val permissionGuard: AppPermissionGuard
) {

    fun hasPermission(): Boolean = permissionGuard.hasUsageStatsPermission(context)

    fun getUsageStats(): Map<String, UsageData> {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val endTime = System.currentTimeMillis()
        val startTime = endTime - MONTH_IN_MILLIS
        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_MONTHLY,
            startTime,
            endTime
        )
        return stats
            .filter { !it.packageName.isNullOrBlank() }
            .associateBy({ it.packageName }) { statsEntry ->
                UsageData(
                    packageName = statsEntry.packageName,
                    totalTimeInForeground = statsEntry.totalTimeInForeground,
                    lastUsed = statsEntry.lastTimeUsed
                )
            }
    }

    private companion object {
        const val MONTH_IN_MILLIS = 30L * 24L * 60L * 60L * 1000L
    }
}
