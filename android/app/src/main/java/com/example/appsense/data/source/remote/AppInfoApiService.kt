package com.example.appsense.data.source.remote

import com.example.appsense.data.source.remote.dto.AppInfoRequestDto
import com.example.appsense.data.source.remote.dto.AppInfoResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AppInfoApiService {

    @POST("/api/v1/app-info")
    suspend fun getAppInfo(@Body request: AppInfoRequestDto): AppInfoResponseDto
}
