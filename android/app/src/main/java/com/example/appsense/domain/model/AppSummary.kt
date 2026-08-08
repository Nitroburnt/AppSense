package com.example.appsense.domain.model

data class AppSummary(
    val packageName: String,
    val purpose: String,
    val keyFeatures: List<String>,
    val alternatives: List<String>,
    val verdict: Verdict,
    val verdictReason: String,
    val cachedAt: Long
)
