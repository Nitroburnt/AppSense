package com.example.appsense.ui.permission

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun PermissionRevokedDialog(
    onOpenSettings: () -> Unit,
    onContinueLimited: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text("Required Access Revoked") },
        text = { Text("AppSense needs Usage Access permission to show screen time and app usage insights. You can grant it again in Settings.") },
        confirmButton = {
            TextButton(onClick = onOpenSettings) {
                Text("Open Settings")
            }
        },
        dismissButton = {
            TextButton(onClick = onContinueLimited) {
                Text("Continue with Limited Features")
            }
        }
    )
}
