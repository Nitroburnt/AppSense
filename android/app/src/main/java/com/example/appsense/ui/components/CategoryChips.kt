package com.example.appsense.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appsense.domain.model.AppCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryChips(
    selected: AppCategory,
    hasUsagePermission: Boolean,
    onSelect: (AppCategory) -> Unit
) {
    LazyRow(contentPadding = PaddingValues(horizontal = 12.dp)) {
        items(AppCategory.entries) { category ->
            val requiresPermission = category == AppCategory.UNUSED || category == AppCategory.MOST_USED
            val enabled = !requiresPermission || hasUsagePermission
            FilterChip(
                selected = selected == category,
                onClick = { onSelect(category) },
                enabled = enabled,
                label = { Text(category.label) },
                leadingIcon = if (requiresPermission && !hasUsagePermission) {
                    {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = "Requires Usage Access",
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else {
                    null
                },
                modifier = Modifier.padding(end = 8.dp)
            )
        }
    }
}
