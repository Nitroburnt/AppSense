package com.example.appsense.domain.repository

import com.example.appsense.domain.model.AppInfo
import kotlinx.coroutines.flow.Flow

interface PackageRepository {
    fun getInstalledApps(): Flow<List<AppInfo>>
    fun getAppInfo(packageName: String): AppInfo?
}
