package com.example.appsense.data.source.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.example.appsense.domain.model.AppSummary
import com.example.appsense.domain.model.Verdict
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromStringList(value: List<String>): String = gson.toJson(value)

    @TypeConverter
    fun toStringList(value: String): List<String> {
        if (value.isEmpty()) return emptyList()
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, type) ?: emptyList()
    }
}

@Entity(tableName = "app_summaries")
data class AppSummaryEntity(
    @PrimaryKey val packageName: String,
    val purpose: String,
    val keyFeatures: List<String>,
    val alternatives: List<String>,
    val verdict: Verdict,
    val verdictReason: String,
    val cachedAt: Long
)

fun AppSummaryEntity.toDomain(): AppSummary = AppSummary(
    packageName = packageName,
    purpose = purpose,
    keyFeatures = keyFeatures,
    alternatives = alternatives,
    verdict = verdict,
    verdictReason = verdictReason,
    cachedAt = cachedAt
)

fun AppSummary.toEntity(): AppSummaryEntity = AppSummaryEntity(
    packageName = packageName,
    purpose = purpose,
    keyFeatures = keyFeatures,
    alternatives = alternatives,
    verdict = verdict,
    verdictReason = verdictReason,
    cachedAt = cachedAt
)
