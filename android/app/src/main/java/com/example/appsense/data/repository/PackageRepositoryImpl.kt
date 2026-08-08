package com.example.appsense.data.repository

import com.example.appsense.data.source.local.PackageManagerDataSource
import com.example.appsense.domain.model.AppInfo
import com.example.appsense.domain.repository.PackageRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow

class PackageRepositoryImpl @Inject constructor(
    private val dataSource: PackageManagerDataSource
) : PackageRepository {

    private val appsChanged = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    override fun getInstalledApps(): Flow<List<AppInfo>> = flow {
        emit(dataSource.getInstalledApps())
        appsChanged.collect { emit(dataSource.getInstalledApps()) }
    }

    override fun getAppInfo(packageName: String): AppInfo? =
        dataSource.getInstalledApps().firstOrNull { it.packageName == packageName }

    fun notifyAppsChanged() {
        appsChanged.tryEmit(Unit)
    }
}
