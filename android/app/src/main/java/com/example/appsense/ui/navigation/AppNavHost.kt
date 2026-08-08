package com.example.appsense.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appsense.ui.dashboard.DashboardScreen
import com.example.appsense.ui.onboarding.OnboardingScreen
import com.example.appsense.ui.settings.SettingsScreen

object AppRoutes {
    const val ONBOARDING = "onboarding"
    const val DASHBOARD = "dashboard"
    const val SETTINGS = "settings"
}

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = AppRoutes.ONBOARDING
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(AppRoutes.ONBOARDING) {
            OnboardingScreen(
                onOnboardingComplete = {
                    navController.navigate(AppRoutes.DASHBOARD) {
                        popUpTo(AppRoutes.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }
        composable(AppRoutes.DASHBOARD) {
            DashboardScreen(onOpenSettings = { navController.navigate(AppRoutes.SETTINGS) })
        }
        composable(AppRoutes.SETTINGS) {
            SettingsScreen()
        }
    }
}
