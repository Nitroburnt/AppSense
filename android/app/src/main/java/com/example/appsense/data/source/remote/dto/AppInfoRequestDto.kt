package com.example.appsense.data.source.remote.dto

import com.google.gson.annotations.SerializedName

data class AppInfoRequestDto(
    @SerializedName("package_name") val packageName: String,
    @SerializedName("app_name") val appName: String
)
