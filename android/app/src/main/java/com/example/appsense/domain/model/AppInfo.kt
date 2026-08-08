package com.example.appsense.domain.model

import android.graphics.drawable.Drawable

data class AppInfo(
    val packageName: String,
    val appName: String,
    val icon: Drawable?,
    val installSource: String?,
    val installDate: Long,
    val appSizeBytes: Long,
    val isSystemApp: Boolean,
    val requestedPermissions: List<String> = emptyList()
)
