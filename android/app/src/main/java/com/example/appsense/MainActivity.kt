package com.example.appsense

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.compose.ui.tooling.preview.Preview
import com.example.appsense.ui.MainViewModel
import com.example.appsense.ui.PermissionState
import com.example.appsense.ui.navigation.AppNavHost
import com.example.appsense.ui.navigation.AppRoutes
import com.example.appsense.ui.permission.PermissionRevokedDialog
import com.example.appsense.ui.settings.SettingsViewModel
import com.example.appsense.ui.theme.AppSenseTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.refreshPermission()
            }
        }
        setContent {
            val darkTheme by settingsViewModel.darkTheme.collectAsState()
            AppSenseTheme(darkTheme = darkTheme) {
                val permissionState by viewModel.permissionState.collectAsState()
                if (permissionState == PermissionState.REVOKED) {
                    PermissionRevokedDialog(
                        onOpenSettings = {
                            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                        },
                        onContinueLimited = viewModel::continueWithLimitedFeatures
                    )
                }
                val startDestination = remember {
                    if (viewModel.isOnboardingComplete()) AppRoutes.DASHBOARD else AppRoutes.ONBOARDING
                }
                AppNavHost(startDestination = startDestination)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppSenseTheme {
        AppNavHost()
    }
}