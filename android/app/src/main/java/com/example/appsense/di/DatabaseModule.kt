package com.example.appsense.di

import android.content.Context
import androidx.room.Room
import com.example.appsense.data.source.local.db.AppDao
import com.example.appsense.data.source.local.db.AppDatabase
import com.example.appsense.data.source.local.db.UninstallLogDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "appsense.db")
            .addMigrations(AppDatabase.MIGRATION_1_2)
            .build()

    @Provides
    fun provideAppDao(database: AppDatabase): AppDao = database.appDao()

    @Provides
    fun provideUninstallLogDao(database: AppDatabase): UninstallLogDao = database.uninstallLogDao()
}
