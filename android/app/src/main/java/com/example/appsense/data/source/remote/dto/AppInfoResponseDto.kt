package com.example.appsense.data.source.remote.dto

import com.google.gson.annotations.SerializedName

data class AppInfoResponseDto(
    @SerializedName("package_name") val packageName: String,
    @SerializedName("purpose") val purpose: String,
    @SerializedName("key_features") val keyFeatures: List<String>,
    @SerializedName("alternatives") val alternatives: List<String>,
    @SerializedName("verdict") val verdict: String,
    @SerializedName("verdict_reason") val verdictReason: String,
    @SerializedName("source") val source: String
)
