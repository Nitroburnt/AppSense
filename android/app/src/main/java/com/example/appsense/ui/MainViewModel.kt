package com.example.appsense.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.appsense.domain.permission.AppPermissionGuard
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class PermissionState { GRANTED, REVOKED, LIMITED }

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val guard: AppPermissionGuard
) : ViewModel() {

    private val _permissionState = MutableStateFlow(computePermissionState())
    val permissionState: StateFlow<PermissionState> = _permissionState.asStateFlow()

    fun refreshPermission() {
        _permissionState.value = computePermissionState()
    }

    fun continueWithLimitedFeatures() {
        _permissionState.value = PermissionState.LIMITED
    }

    fun isOnboardingComplete(): Boolean = guard.isOnboardingComplete(context)

    private fun computePermissionState(): PermissionState = when {
        guard.hasUsageStatsPermission(context) -> PermissionState.GRANTED
        guard.isOnboardingComplete(context) -> PermissionState.REVOKED
        else -> PermissionState.LIMITED
    }
}
