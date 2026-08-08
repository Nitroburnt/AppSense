package com.example.appsense.ui.permission

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNode
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.performKeyPress
import android.view.KeyEvent
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

class PermissionRevokedDialogTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `dialog shows required access revoked title and is not dismissable on back press`() {
        composeRule.setContent {
            PermissionRevokedDialog(
                onOpenSettings = {},
                onContinueLimited = {}
            )
        }

        composeRule
            .onNode(hasText("Required Access Revoked"))
            .assertIsDisplayed()

        composeRule
            .onNode(hasText("AppSense needs Usage Access permission"))
            .assertIsDisplayed()

        composeRule
            .onNode(hasText("Open Settings"))
            .assertIsDisplayed()

        composeRule
            .onNode(hasText("Continue with Limited Features"))
            .assertIsDisplayed()

        composeRule.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK))
        
        // Dialog should still be visible (not dismissable)
        composeRule
            .onNode(hasText("Required Access Revoked"))
            .assertIsDisplayed()
    }
}