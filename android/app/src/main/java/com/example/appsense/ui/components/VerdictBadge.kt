package com.example.appsense.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.appsense.domain.model.Verdict

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerdictBadge(verdict: Verdict, reason: String, modifier: Modifier = Modifier) {
    val tooltipState = rememberTooltipState()
    val color = when (verdict) {
        Verdict.KEEP -> Color(0xFF2E7D32)
        Verdict.UNINSTALL -> Color(0xFFC62828)
        Verdict.NEUTRAL -> Color(0xFF757575)
    }
    TooltipBox(
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = { PlainTooltip { Text(reason) } },
        state = tooltipState,
        modifier = modifier
    ) {
        Surface(
            color = color,
            contentColor = Color.White,
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = verdict.name,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}
