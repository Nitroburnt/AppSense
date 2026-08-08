package com.example.appsense.ui.components

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
import androidx.compose.ui.unit.dp
import android.graphics.drawable.Drawable
import com.example.appsense.domain.model.AppInfo
import com.example.appsense.domain.model.AppSummary
import com.example.appsense.ui.settings.SettingsViewModel
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNode
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.onNodeWithContentDescription
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*
import io.mockk.mockk
import io.mockk.every

@OptIn(ExperimentalMaterial3Api::class)
class AppCardTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun createAppInfo(
        packageName: String = "com.test.app",
        appName: String = "Test App",
        isSystemApp: Boolean = false
    ): AppInfo {
        return AppInfo(
            packageName = packageName,
            appName = appName,
            icon = null,
            installSource = "com.android.vending",
            installDate = System.currentTimeMillis(),
            appSizeBytes = 1000000,
            isSystemApp = isSystemApp,
            requestedPermissions = emptyList()
        )
    }

    @Test
    fun `expansion drawer is hidden by default`() {
        val app = createAppInfo()

        composeRule.setContent {
            MaterialTheme {
                AppCard(
                    app = app,
                    summary = null,
                    isSummaryLoading = false,
                    onExpand = {}
                )
            }
        }

        composeRule
            .onNodeWithContentDescription("Uninstall Test App")
            .assertIsDisplayed()

        composeRule
            .onNode(hasText("Test purpose"))
            .assertDoesNotExist()
    }

    @Test
    fun `tap card shows purpose text`() {
        val app = createAppInfo()
        val summary = AppSummary(
            packageName = "com.test.app",
            purpose = "Test purpose",
            keyFeatures = listOf("Feature 1"),
            alternatives = listOf("Alt 1"),
            verdict = com.example.appsense.domain.model.Verdict.KEEP,
            verdictReason = "Test reason",
            cachedAt = System.currentTimeMillis()
        )

        composeRule.setContent {
            MaterialTheme {
                AppCard(
                    app = app,
                    summary = summary,
                    isSummaryLoading = false,
                    onExpand = {}
                )
            }
        }

        composeRule
            .onNode(hasText("Test App"))
            .performClick()

        composeRule
            .onNode(hasText("Test purpose"))
            .assertIsDisplayed()
    }
}