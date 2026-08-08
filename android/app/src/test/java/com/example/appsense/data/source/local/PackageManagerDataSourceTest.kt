package com.example.appsense.data.source.local

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.test.core.app.ApplicationProvider
import com.example.appsense.data.source.local.InstallSourceChecker
import com.example.appsense.domain.model.AppInfo
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class PackageManagerDataSourceTest {

    private lateinit var context: Context
    private lateinit var packageManager: PackageManager
    private lateinit var dataSource: PackageManagerDataSource
    private lateinit var installSourceChecker: InstallSourceChecker

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<Context>()
        packageManager = context.packageManager
        installSourceChecker = InstallSourceChecker()
        dataSource = PackageManagerDataSource(context)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `system apps are filtered out via FLAG_SYSTEM`() {
        val apps = dataSource.getInstalledApps()
        
        val systemApps = apps.filter { it.isSystemApp }
        val userApps = apps.filter { !it.isSystemApp }
        
        assertTrue("Should have at least some user apps", userApps.isNotEmpty())
        
        systemApps.forEach { app ->
            assertTrue("App ${app.packageName} should be marked as system", app.isSystemApp)
        }
        
        userApps.forEach { app ->
            assertFalse("App ${app.packageName} should not be marked as system", app.isSystemApp)
        }
    }

    @Test
    fun `sideloaded detection returns correct boolean`() {
        val apps = dataSource.getInstalledApps()
        
        val sideloadedApps = apps.filter { installSourceChecker.isSideloaded(it.installSource) }
        val storeApps = apps.filter { !installSourceChecker.isSideloaded(it.installSource) }
        
        assertTrue("Should have at least some apps", apps.isNotEmpty())
        
        sideloadedApps.forEach { app ->
            assertTrue("App ${app.packageName} with source ${app.installSource} should be sideloaded", 
                installSourceChecker.isSideloaded(app.installSource))
        }
        
        storeApps.forEach { app ->
            assertFalse("App ${app.packageName} with source ${app.installSource} should not be sideloaded", 
                installSourceChecker.isSideloaded(app.installSource))
        }
    }

    @Test
    fun `install source checker known stores`() {
        assertTrue(installSourceChecker.isFromKnownStore("com.android.vending"))
        assertTrue(installSourceChecker.isFromKnownStore("org.fdroid.fdroid"))
        assertTrue(installSourceChecker.isFromKnownStore("com.aurora.store"))
        assertTrue(installSourceChecker.isFromKnownStore("com.sec.android.app.samsungapps"))
        assertTrue(installSourceChecker.isFromKnownStore("com.amazon.venezia"))
        assertTrue(installSourceChecker.isFromKnownStore("com.huawei.appmarket"))
        
        assertFalse(installSourceChecker.isFromKnownStore("com.unknown.store"))
        assertFalse(installSourceChecker.isFromKnownStore(null))
        assertFalse(installSourceChecker.isFromKnownStore(""))
    }
}