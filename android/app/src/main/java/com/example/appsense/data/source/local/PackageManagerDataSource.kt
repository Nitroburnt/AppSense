package com.example.appsense.data.source.local

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import com.example.appsense.domain.model.AppInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class PackageManagerDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val packageManager: PackageManager = context.packageManager

    fun getInstalledApps(): List<AppInfo> {
        val flags = PackageManager.PackageInfoFlags.of(PackageManager.GET_PERMISSIONS.toLong())
        val packages = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getInstalledPackages(flags)
        } else {
            @Suppress("DEPRECATION")
            packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS)
        }
        return packages.mapNotNull { packageInfo ->
            val applicationInfo = packageInfo.applicationInfo ?: return@mapNotNull null
            AppInfo(
                packageName = packageInfo.packageName,
                appName = applicationInfo.loadLabel(packageManager).toString(),
                icon = applicationInfo.loadIcon(packageManager),
                installSource = resolveInstallSource(packageInfo.packageName),
                installDate = packageInfo.firstInstallTime,
                appSizeBytes = File(applicationInfo.sourceDir).length(),
                isSystemApp = (applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0,
                requestedPermissions = packageInfo.requestedPermissions?.toList() ?: emptyList()
            )
        }
    }

    private fun resolveInstallSource(packageName: String): String? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                packageManager.getInstallSourceInfo(packageName).installingPackageName
            } catch (_: Exception) {
                null
            }
        } else {
            @Suppress("DEPRECATION")
            packageManager.getInstallerPackageName(packageName)
        }
}
