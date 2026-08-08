package com.example.appsense.data.source.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UninstallLogDao {

    @Insert
    suspend fun insertLog(log: UninstallLogEntity)

    @Query("SELECT * FROM uninstall_log ORDER BY uninstalledAt DESC")
    fun getAllLogs(): Flow<List<UninstallLogEntity>>
}
