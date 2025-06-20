package com.example.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState // Added import
import androidx.compose.foundation.verticalScroll // Added import
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource // Added import
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import dev.aurakai.auraframefx.R // Added import
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.app.viewmodel.OracleDriveControlViewModel
import kotlinx.coroutines.launch

/**
 * Displays a UI screen for controlling and monitoring the Oracle Drive service.
 *
 * Provides controls to refresh service status, view diagnostics logs, and enable or disable modules by package name. The screen reflects real-time connection status and displays error messages for failed operations.
 */
@Composable
public fun OracleDriveControlScreen(
    viewModel: OracleDriveControlViewModel = hiltViewModel(),
) {
    public val context = LocalContext.current
    public val isConnected by viewModel.isServiceConnected.collectAsState()
    public val status by viewModel.status.collectAsState()
    public val detailedStatus by viewModel.detailedStatus.collectAsState()
    public val diagnosticsLog by viewModel.diagnosticsLog.collectAsState()
    public var packageName by remember { mutableStateOf(TextFieldValue("")) }
    public var enableModule by remember { mutableStateOf(true) }
    public var isLoading by remember { mutableStateOf(false) }
    public var errorMessage by remember { mutableStateOf<String?>(null) }
    public val logScrollState: rememberScrollState = rememberScrollState()
    public val viewModelScope: rememberCoroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.bindService()
        viewModel.refreshStatus()
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.unbindService() }
    }

    // --- UI logic for actions ---
    suspend fun safeRefresh() {
        isLoading = true
        errorMessage = null
        try {
            viewModel.refreshStatus()
        } catch (e: Exception) {
            errorMessage =
                context.getString(R.string.failed_to_refresh, e.localizedMessage ?: e.toString())
        } finally {
            isLoading = false
        }
    }

    suspend fun safeToggle() {
        if (packageName.text.isBlank()) return
        isLoading = true
        errorMessage = null
        try {
            viewModel.toggleModule(packageName.text, enableModule)
        } catch (e: Exception) {
            errorMessage =
                context.getString(R.string.failed_to_toggle, e.localizedMessage ?: e.toString())
        } finally {
            isLoading = false
        }
    }

    // --- UI ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (isConnected) stringResource(dev.aurakai.auraframefx.R.string.oracle_drive_connected) else stringResource(
                dev.aurakai.auraframefx.R.string.oracle_drive_not_connected
            ),
            style = MaterialTheme.typography.titleMedium,
            color = if (isConnected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )
        if (isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(
                onClick = { viewModelScope.launch { safeRefresh() } },
                enabled = isConnected && !isLoading
            ) {
                Text(stringResource(R.string.refresh_status))
            }
        }
        Divider()
        Text(stringResource(R.string.status_label, status ?: "-"))
        Text(stringResource(R.string.detailed_status_label, detailedStatus ?: "-"))
        Text(
            stringResource(R.string.diagnostics_log_label),
            style = MaterialTheme.typography.labelMedium
        )
        Box(
            modifier = Modifier
                .height(120.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = diagnosticsLog ?: "-",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.verticalScroll(logScrollState)
            )
        }
        Divider()
        Text(
            stringResource(R.string.toggle_module_label),
            style = MaterialTheme.typography.titleSmall
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = packageName,
                onValueChange = { packageName = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                singleLine = true,
                label = { Text(stringResource(R.string.module_package_name)) },
                enabled = isConnected && !isLoading
            )
            Switch(
                checked = enableModule,
                onCheckedChange = { enableModule = it },
                enabled = isConnected && !isLoading
            )
            Button(
                onClick = { viewModelScope.launch { safeToggle() } },
                enabled = isConnected && packageName.text.isNotBlank() && !isLoading,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(stringResource(if (enableModule) R.string.enable else R.string.disable))
            }
        }
    }
}
