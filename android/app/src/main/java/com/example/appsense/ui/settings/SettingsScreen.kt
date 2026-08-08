package com.example.appsense.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val viewModel: SettingsViewModel = hiltViewModel(viewModelStoreOwner = context as ViewModelStoreOwner)
    val darkTheme by viewModel.darkTheme.collectAsState()
    val geminiKey by viewModel.geminiApiKey.collectAsState()
    val openAiKey by viewModel.openAiApiKey.collectAsState()
    val uninstallLogs by viewModel.uninstallLogs.collectAsState(initial = emptyList())

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)

        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Dark theme", modifier = Modifier.weight(1f))
            Switch(checked = darkTheme, onCheckedChange = viewModel::setDarkTheme)
        }

        HorizontalDivider()

        Text(
            "Your API keys (Bring Your Own Key)",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 12.dp)
        )
        OutlinedTextField(
            value = geminiKey,
            onValueChange = viewModel::setGeminiApiKey,
            label = { Text("Gemini API Key") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = openAiKey,
            onValueChange = viewModel::setOpenAiApiKey,
            label = { Text("OpenAI API Key") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        HorizontalDivider(Modifier.padding(vertical = 12.dp))

        LinkRow("About", "https://github.com/anomalyco/opencode")
        LinkRow("Feedback", "https://github.com/anomalyco/opencode/issues")

        HorizontalDivider(Modifier.padding(vertical = 12.dp))

        Text("Uninstall Log", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        if (uninstallLogs.isEmpty()) {
            Text("No uninstalls recorded yet.", style = MaterialTheme.typography.bodyMedium)
        } else {
            uninstallLogs.forEach { log ->
                Text(
                    text = "${log.appName} · ${formatTime(log.uninstalledAt)}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = log.packageName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun LinkRow(label: String, url: String) {
    val context = LocalContext.current
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) }
            .padding(vertical = 12.dp)
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
    }
}

private fun formatTime(timestamp: Long): String =
    SimpleDateFormat("MMM d, yyyy HH:mm", Locale.getDefault()).format(Date(timestamp))
