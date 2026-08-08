package com.example.appsense.data.repository

import com.example.appsense.data.source.local.UsageStatsDataSource
import com.example.appsense.domain.model.UsageData
import com.example.appsense.domain.repository.UsageStatsRepository
import javax.inject.Inject

class UsageStatsRepositoryImpl @Inject constructor(
    private val dataSource: UsageStatsDataSource
) : UsageStatsRepository {

    override fun getUsageStats(): Map<String, UsageData> = dataSource.getUsageStats()

    override fun hasPermission(): Boolean = dataSource.hasPermission()
}
