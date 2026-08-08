package com.example.appsense.domain.repository

import com.example.appsense.domain.model.AppSummary
import kotlinx.coroutines.flow.Flow

interface AppSummaryRepository {
    fun getSummary(packageName: String, appName: String): Flow<Result<AppSummary>>
}
