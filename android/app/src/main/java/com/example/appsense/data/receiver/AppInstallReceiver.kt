package com.example.appsense.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.appsense.data.repository.PackageRepositoryImpl
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject

class AppInstallReceiver @Inject constructor(
    private val packageRepository: PackageRepositoryImpl
) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        if (action == Intent.ACTION_PACKAGE_ADDED || action == Intent.ACTION_PACKAGE_REMOVED) {
            packageRepository.notifyAppsChanged()
        }
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AppInstallReceiverEntryPoint {
    fun appInstallReceiver(): AppInstallReceiver
}
