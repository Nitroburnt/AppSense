package com.example.appsense.ui.dashboard

import android.content.IntentFilter
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.appsense.data.receiver.AppInstallReceiverEntryPoint
import com.example.appsense.ui.components.AppCard
import com.example.appsense.ui.components.AppTopBar
import com.example.appsense.ui.components.CategoryChips
import com.example.appsense.ui.components.SortBottomSheet
import dagger.hilt.android.EntryPointAccessors

@Composable
fun DashboardScreen(onOpenSettings: () -> Unit) {
    val viewModel: DashboardViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()
    val visibleApps = remember(state) { viewModel.visibleApps }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var showSortSheet by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.refreshUsage()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    DisposableEffect(lifecycleOwner) {
        val receiver = EntryPointAccessors.fromApplication(
            context.applicationContext,
            AppInstallReceiverEntryPoint::class.java
        ).appInstallReceiver()
        val filter = IntentFilter().apply {
            addAction(android.content.Intent.ACTION_PACKAGE_ADDED)
            addAction(android.content.Intent.ACTION_PACKAGE_REMOVED)
            addDataScheme("package")
        }
        context.registerReceiver(receiver, filter)
        onDispose { context.unregisterReceiver(receiver) }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { AppTopBar(onOpenSettings = onOpenSettings) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showSortSheet = true }) {
                Text("Sort")
            }
        }
    ) { innerPadding ->
        Column(Modifier.padding(innerPadding)) {
            CategoryChips(
                selected = state.activeCategory,
                hasUsagePermission = state.hasUsagePermission,
                onSelect = viewModel::selectCategory
            )
            when {
                state.isLoading && visibleApps.isEmpty() -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
                state.error != null && visibleApps.isEmpty() -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.error ?: "Something went wrong")
                }
                else -> LazyColumn(Modifier.fillMaxSize()) {
                    items(visibleApps, key = { it.packageName }) { app ->
                        AppCard(
                            app = app,
                            summary = state.summaries[app.packageName],
                            isSummaryLoading = app.packageName in state.loadingSummaries,
                            onExpand = viewModel::onAppExpanded
                        )
                    }
                }
            }
        }
    }

    if (showSortSheet) {
        SortBottomSheet(
            selected = state.sortOrder,
            onSelect = {
                viewModel.selectSortOrder(it)
                showSortSheet = false
            },
            onDismiss = { showSortSheet = false }
        )
    }
}
