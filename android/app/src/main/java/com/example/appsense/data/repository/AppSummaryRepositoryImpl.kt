package com.example.appsense.data.repository

import com.example.appsense.data.source.local.db.AppDao
import com.example.appsense.data.source.local.db.toDomain
import com.example.appsense.data.source.local.db.toEntity
import com.example.appsense.data.source.remote.AppInfoApiService
import com.example.appsense.data.source.remote.dto.AppInfoRequestDto
import com.example.appsense.data.source.remote.dto.AppInfoResponseDto
import com.example.appsense.domain.model.AppSummary
import com.example.appsense.domain.model.Verdict
import com.example.appsense.domain.repository.AppSummaryRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

class AppSummaryRepositoryImpl @Inject constructor(
    private val dao: AppDao,
    private val api: AppInfoApiService
) : AppSummaryRepository {

    override fun getSummary(packageName: String, appName: String): Flow<Result<AppSummary>> = flow {
        val cached = dao.getSummaryByPackage(packageName).firstOrNull()
        if (cached != null) {
            emit(Result.success(cached.toDomain()))
            return@flow
        }
        try {
            val dto = api.getAppInfo(AppInfoRequestDto(packageName = packageName, appName = appName))
            val summary = dto.toDomain(packageName)
            dao.upsertSummary(summary.toEntity())
            emit(Result.success(summary))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    private fun AppInfoResponseDto.toDomain(packageName: String): AppSummary = AppSummary(
        packageName = packageName,
        purpose = purpose,
        keyFeatures = keyFeatures,
        alternatives = alternatives,
        verdict = verdict.toVerdict(),
        verdictReason = verdictReason,
        cachedAt = System.currentTimeMillis()
    )

    private fun String.toVerdict(): Verdict = when (uppercase()) {
        "KEEP" -> Verdict.KEEP
        "UNINSTALL" -> Verdict.UNINSTALL
        else -> Verdict.NEUTRAL
    }
}
