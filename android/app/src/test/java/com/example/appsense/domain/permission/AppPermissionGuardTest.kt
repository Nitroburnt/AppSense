package com.example.appsense.domain.permission

import android.app.AppOpsManager
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class AppPermissionGuardTest {

    private lateinit var context: Context
    private lateinit var guard: AppPermissionGuard
    private lateinit var appOpsManager: AppOpsManager

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<Context>()
        guard = AppPermissionGuard()
        appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    }

    @Test
    fun `hasUsageStatsPermission returns false when mode is MODE_IGNORED`() {
        val mockAppOps = mockk<AppOpsManager>()
        every { mockAppOps.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, any(), any()) } returns AppOpsManager.MODE_IGNORED
        
        val guardWithMock = AppPermissionGuard()
        
        val hasPermission = guardWithMock.hasUsageStatsPermission(context)
        
        assertFalse("Should return false when MODE_IGNORED", hasPermission)
    }

    @Test
    fun `hasUsageStatsPermission returns true when mode is MODE_ALLOWED`() {
        val mockAppOps = mockk<AppOpsManager>()
        every { mockAppOps.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, any(), any()) } returns AppOpsManager.MODE_ALLOWED
        
        val guardWithMock = AppPermissionGuard()
        
        val hasPermission = guardWithMock.hasUsageStatsPermission(context)
        
        assertTrue("Should return true when MODE_ALLOWED", hasPermission)
    }

    @Test
    fun `hasUsageStatsPermission returns false when mode is MODE_ERRORED`() {
        val mockAppOps = mockk<AppOpsManager>()
        every { mockAppOps.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, any(), any()) } returns AppOpsManager.MODE_ERRORED
        
        val guardWithMock = AppPermissionGuard()
        
        val hasPermission = guardWithMock.hasUsageStatsPermission(context)
        
        assertFalse("Should return false when MODE_ERRORED", hasPermission)
    }

    @Test
    fun `hasUsageStatsPermission returns false when mode is MODE_DEFAULT`() {
        val mockAppOps = mockk<AppOpsManager>()
        every { mockAppOps.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, any(), any()) } returns AppOpsManager.MODE_DEFAULT
        
        val guardWithMock = AppPermissionGuard()
        
        val hasPermission = guardWithMock.hasUsageStatsPermission(context)
        
        assertFalse("Should return false when MODE_DEFAULT (not explicitly granted)", hasPermission)
    }
}