package com.example.appsense.ui.dashboard

import com.example.appsense.data.source.local.InstallSourceChecker
import com.example.appsense.domain.model.AppCategory
import com.example.appsense.domain.model.AppInfo
import com.example.appsense.domain.model.AppSummary
import com.example.appsense.domain.model.SortOrder
import com.example.appsense.domain.model.UsageData
import com.example.appsense.domain.model.Verdict
import com.example.appsense.domain.repository.AppSummaryRepository
import com.example.appsense.domain.repository.PackageRepository
import com.example.appsense.domain.repository.UsageStatsRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.*

class DashboardViewModelTest {

    private fun createAppInfo(
        packageName: String,
        appName: String,
        isSystemApp: Boolean = false,
        installSource: String? = "com.android.vending",
        installDate: Long = 1000000000L,
        appSizeBytes: Long = 1000000L
    ): AppInfo {
        return AppInfo(
            packageName = packageName,
            appName = appName,
            icon = null,
            installSource = installSource,
            installDate = installDate,
            appSizeBytes = appSizeBytes,
            isSystemApp = isSystemApp,
            requestedPermissions = emptyList()
        )
    }

    private fun createUsageData(packageName: String, totalTime: Long, lastUsed: Long = System.currentTimeMillis()): UsageData {
        return UsageData(packageName = packageName, totalTimeInForeground = totalTime, lastUsed = lastUsed)
    }

    @Test
    fun `NAME_ASC sort produces alphabetically ordered list`() = runTest {
        val packageRepo = mockk<PackageRepository>()
        val usageStatsRepo = mockk<UsageStatsRepository>()
        val installSourceChecker = InstallSourceChecker()
        val appSummaryRepo = mockk<AppSummaryRepository>()

        val apps = listOf(
            createAppInfo("com.third", "Third App", installDate = 3000),
            createAppInfo("com.first", "First App", installDate = 1000),
            createAppInfo("com.second", "Second App", installDate = 2000)
        )

        every { packageRepo.getInstalledApps() } returns kotlinx.coroutines.flow.flowOf(apps)
        every { usageStatsRepo.hasPermission() } returns true
        every { usageStatsRepo.getUsageStats() } returns emptyMap()

        val viewModel = DashboardViewModel(packageRepo, usageStatsRepo, installSourceChecker, mockk())

        advanceUntilIdle()

        val names = viewModel.visibleApps.map { it.appName }
        assertEquals(listOf("First App", "Second App", "Third App"), names)
    }

    @Test
    fun `USAGE_HIGH sort orders by totalTimeInForeground descending`() = runTest {
        val packageRepo = mockk<PackageRepository>()
        val usageStatsRepo = mockk<UsageStatsRepository>()
        val installSourceChecker = InstallSourceChecker()
        val appSummaryRepo = mockk<AppSummaryRepository>()

        val apps = listOf(
            createAppInfo("com.low", "Low Usage", installDate = 1000),
            createAppInfo("com.high", "High Usage", installDate = 2000),
            createAppInfo("com.medium", "Medium Usage", installDate = 3000)
        )

        val usageStats = mapOf(
            "com.low" to createUsageData("com.low", 100),
            "com.high" to createUsageData("com.high", 1000),
            "com.medium" to createUsageData("com.medium", 500)
        )

        every { packageRepo.getInstalledApps() } returns kotlinx.coroutines.flow.flowOf(apps)
        every { usageStatsRepo.hasPermission() } returns true
        every { usageStatsRepo.getUsageStats() } returns usageStats

        val viewModel = DashboardViewModel(packageRepo, usageStatsRepo, InstallSourceChecker(), mockk())

        advanceUntilIdle()

        viewModel.selectSortOrder(SortOrder.USAGE_HIGH)
        advanceUntilIdle()

        val names = viewModel.visibleApps.map { it.appName }
        assertEquals(listOf("High Usage", "Medium Usage", "Low Usage"), names)
    }

    @Test
    fun `NAME_DESC sort produces reverse alphabetically ordered list`() = runTest {
        val packageRepo = mockk<PackageRepository>()
        val usageStatsRepo = mockk<UsageStatsRepository>()
        val installSourceChecker = InstallSourceChecker()
        val appSummaryRepo = mockk<AppSummaryRepository>()

        val apps = listOf(
            createAppInfo("com.third", "Third App"),
            createAppInfo("com.first", "First App"),
            createAppInfo("com.second", "Second App")
        )

        every { packageRepo.getInstalledApps() } returns kotlinx.coroutines.flow.flowOf(apps)
        every { usageStatsRepo.hasPermission() } returns true
        every { usageStatsRepo.getUsageStats() } returns emptyMap()

        val viewModel = DashboardViewModel(packageRepo, usageStatsRepo, installSourceChecker, mockk())

        advanceUntilIdle()

        viewModel.selectSortOrder(SortOrder.NAME_DESC)
        advanceUntilIdle()

        val names = viewModel.visibleApps.map { it.appName }
        assertEquals(listOf("Third App", "Second App", "First App"), names)
    }

    @Test
    fun `SIDELOADED category filters correctly`() = runTest {
        val packageRepo = mockk<PackageRepository>()
        val usageStatsRepo = mockk<UsageStatsRepository>()
        val installSourceChecker = InstallSourceChecker()
        val appSummaryRepo = mockk<AppSummaryRepository>()

        val apps = listOf(
            createAppInfo("com.play", "Play Store App", installSource = "com.android.vending"),
            createAppInfo("com.sideloaded", "Sideloaded App", installSource = "com.unknown.installer"),
            createAppInfo("com.fdroid", "F-Droid App", installSource = "org.fdroid.fdroid")
        )

        every { packageRepo.getInstalledApps() } returns kotlinx.coroutines.flow.flowOf(apps)
        every { usageStatsRepo.hasPermission() } returns true
        every { usageStatsRepo.getUsageStats() } returns emptyMap()

        val viewModel = DashboardViewModel(packageRepo, usageStatsRepo, installSourceChecker, mockk())

        advanceUntilIdle()

        viewModel.selectCategory(AppCategory.SIDELOADED)
        advanceUntilIdle()

        val names = viewModel.visibleApps.map { it.appName }
        assertEquals(listOf("Sideloaded App"), names)
    }

    @Test
    fun `UNUSED category filters apps with zero usage time`() = runTest {
        val packageRepo = mockk<PackageRepository>()
        val usageStatsRepo = mockk<UsageStatsRepository>()
        val installSourceChecker = InstallSourceChecker()
        val appSummaryRepo = mockk<AppSummaryRepository>()

        val apps = listOf(
            createAppInfo("com.used", "Used App"),
            createAppInfo("com.unused", "Unused App"),
            createAppInfo("com.alsoUnused", "Also Unused")
        )

        val usageStats = mapOf(
            "com.used" to createUsageData("com.used", 1000),
            "com.unused" to createUsageData("com.unused", 0),
        )

        every { packageRepo.getInstalledApps() } returns kotlinx.coroutines.flow.flowOf(apps)
        every { usageStatsRepo.hasPermission() } returns true
        every { usageStatsRepo.getUsageStats() } returns usageStats

        val viewModel = DashboardViewModel(packageRepo, usageStatsRepo, installSourceChecker, mockk())

        advanceUntilIdle()

        viewModel.selectCategory(AppCategory.UNUSED)
        advanceUntilIdle()

        val packageNames = viewModel.visibleApps.map { it.packageName }
        assertTrue(packageNames.contains("com.unused"))
        assertTrue(packageNames.contains("com.alsoUnused"))
        assertFalse(packageNames.contains("com.used"))
    }
}