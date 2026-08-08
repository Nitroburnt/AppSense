package com.example.appsense.domain.model

data class UsageData(
    val packageName: String,
    val totalTimeInForeground: Long,
    val lastUsed: Long
)
