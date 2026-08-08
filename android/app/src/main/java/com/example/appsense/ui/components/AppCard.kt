package com.example.appsense.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import android.graphics.drawable.Drawable
import com.example.appsense.domain.model.AppInfo
import com.example.appsense.domain.model.AppSummary
import com.example.appsense.ui.settings.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppCard(
    app: AppInfo,
    summary: AppSummary?,
    isSummaryLoading: Boolean,
    onExpand: (AppInfo) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val context = LocalContext.current
    Card(
        onClick = {
            val willExpand = !expanded
            expanded = willExpand
            if (willExpand && summary == null && !isSummaryLoading) onExpand(app)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Column(Modifier.fillMaxWidth().padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                app.icon?.let {
                    Image(
                        bitmap = it.toImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                }
                Column(Modifier.weight(1f).padding(start = 12.dp)) {
                    Text(app.appName, style = MaterialTheme.typography.titleMedium)
                    Text(
                        app.packageName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    PrivacyBadges(app)
                }
                if (app.isSystemApp) {
                    Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                        Text(
                            "System App",
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                } else {
                    IconButton(onClick = {
                        settingsViewModel.logUninstall(app)
                        context.startActivity(
                            Intent(Intent.ACTION_DELETE, Uri.parse("package:${app.packageName}"))
                        )
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Uninstall ${app.appName}")
                    }
                }
            }
            AnimatedVisibility(visible = expanded) {
                Column(Modifier.padding(top = 8.dp)) {
                    when {
                        summary != null -> SummaryContent(summary)
                        isSummaryLoading -> Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp)
                            Spacer(Modifier.width(8.dp))
                            Text("Analyzing...", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PrivacyBadges(app: AppInfo) {
    val labels = app.requestedPermissions.mapNotNull { permissionLabel(it) }
    if (labels.isEmpty()) return
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        labels.forEach { label ->
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            ) {
                Text(
                    label,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun SummaryContent(summary: AppSummary) {
    Column {
        Text(summary.purpose, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(8.dp))
        Text("Key features", style = MaterialTheme.typography.labelLarge)
        summary.keyFeatures.forEach { Text("• $it", style = MaterialTheme.typography.bodySmall) }
        Spacer(Modifier.height(8.dp))
        Text("Alternatives", style = MaterialTheme.typography.labelLarge)
        summary.alternatives.forEach { Text("• $it", style = MaterialTheme.typography.bodySmall) }
        Spacer(Modifier.height(8.dp))
        VerdictBadge(verdict = summary.verdict, reason = summary.verdictReason)
    }
}

private fun Drawable.toImageBitmap(): ImageBitmap {
    val width = if (intrinsicWidth > 0) intrinsicWidth else 64
    val height = if (intrinsicHeight > 0) intrinsicHeight else 64
    return toBitmap(width, height).asImageBitmap()
}

private fun permissionLabel(permission: String): String? = when (permission) {
    "android.permission.ACCESS_FINE_LOCATION",
    "android.permission.ACCESS_COARSE_LOCATION" -> "Location"
    "android.permission.CAMERA" -> "Camera"
    "android.permission.RECORD_AUDIO" -> "Microphone"
    "android.permission.READ_CONTACTS" -> "Contacts"
    else -> null
}
