package com.example.appsense.data.source.local.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    @Upsert
    suspend fun upsertSummary(entity: AppSummaryEntity)

    @Query("SELECT * FROM app_summaries WHERE packageName = :packageName")
    fun getSummaryByPackage(packageName: String): Flow<AppSummaryEntity?>

    @Query("SELECT packageName FROM app_summaries")
    suspend fun getAllCachedPackageNames(): List<String>

    @Query("DELETE FROM app_summaries WHERE packageName = :packageName")
    suspend fun deleteSummary(packageName: String)
}
