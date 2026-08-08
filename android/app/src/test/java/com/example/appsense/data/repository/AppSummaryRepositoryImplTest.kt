package com.example.appsense.data.repository

import com.example.appsense.data.source.local.db.AppDao
import com.example.appsense.data.source.local.db.AppSummaryEntity
import com.example.appsense.data.source.local.db.toEntity
import com.example.appsense.data.source.remote.AppInfoApiService
import com.example.appsense.data.source.remote.dto.AppInfoRequestDto
import com.example.appsense.data.source.remote.dto.AppInfoResponseDto
import com.example.appsense.domain.model.AppSummary
import com.example.appsense.domain.model.Verdict
import com.example.appsense.domain.repository.AppSummaryRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert.*

class AppSummaryRepositoryImplTest {

    private fun createSummary(packageName: String): AppSummary = AppSummary(
        packageName = packageName,
        purpose = "Test purpose",
        keyFeatures = listOf("Feature 1", "Feature 2"),
        alternatives = listOf("Alt 1"),
        verdict = Verdict.KEEP,
        verdictReason = "Test reason",
        cachedAt = System.currentTimeMillis()
    )

    private fun createDto(packageName: String): AppInfoResponseDto = AppInfoResponseDto(
        packageName = packageName,
        purpose = "Test purpose",
        keyFeatures = listOf("Feature 1", "Feature 2"),
        alternatives = listOf("Alt 1"),
        verdict = "KEEP",
        verdictReason = "Test reason",
        source = "test"
    )

    @Test
    fun `cache hit path does not call network`() = runBlocking {
        val appDao = mockk<AppDao>(relaxed = true)
        val apiService = mockk<AppInfoApiService>()

        val cachedSummary = createSummary("com.test.app")
        every { appDao.getSummaryByPackage("com.test.app") } returns kotlinx.coroutines.flow.flowOf(cachedSummary.toEntity())

        val repository = AppSummaryRepositoryImpl(appDao, apiService)

        val result: Result<AppSummary> = repository.getSummary("com.test.app", "Test App").first()

        assertTrue(result.isSuccess)
        val summary: AppSummary = result.getOrNull()!!
        assertEquals("com.test.app", summary.packageName)

        verify(exactly = 0) { apiService.getAppInfo(capture()) }
        verify(exactly = 1) { appDao.getSummaryByPackage("com.test.app") }
    }

    @Test
    fun `cache miss path calls network and upserts to Room`() = runBlocking {
        val appDao = mockk<AppDao>(relaxed = true)
        val apiService = mockk<AppInfoApiService>()

        every { appDao.getSummaryByPackage("com.test.app") } returns kotlinx.coroutines.flow.flowOf(null)
        every { apiService.getAppInfo(any()) } returns createDto("com.test.app")

        val repository = AppSummaryRepositoryImpl(appDao, apiService)

        val result: Result<AppSummary> = repository.getSummary("com.test.app", "Test App").first()

        assertTrue(result.isSuccess)
        val summary: AppSummary = result.getOrNull()!!
        assertEquals("com.test.app", summary.packageName)
        assertEquals(Verdict.KEEP, summary.verdict)

        verify(exactly = 1) { apiService.getAppInfo(capture()) }
        verify(exactly = 1) { appDao.getSummaryByPackage("com.test.app") }
    }
}