package com.example.appsense.di

import com.example.appsense.data.repository.AppSummaryRepositoryImpl
import com.example.appsense.data.repository.PackageRepositoryImpl
import com.example.appsense.data.repository.UsageStatsRepositoryImpl
import com.example.appsense.domain.repository.AppSummaryRepository
import com.example.appsense.domain.repository.PackageRepository
import com.example.appsense.domain.repository.UsageStatsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindPackageRepository(impl: PackageRepositoryImpl): PackageRepository

    @Binds
    abstract fun bindUsageStatsRepository(impl: UsageStatsRepositoryImpl): UsageStatsRepository

    @Binds
    abstract fun bindAppSummaryRepository(impl: AppSummaryRepositoryImpl): AppSummaryRepository
}
