package com.example.appsense.di

import com.example.appsense.domain.permission.AppPermissionGuard
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PermissionModule {

    @Provides
    @Singleton
    fun provideAppPermissionGuard(): AppPermissionGuard = AppPermissionGuard()
}
