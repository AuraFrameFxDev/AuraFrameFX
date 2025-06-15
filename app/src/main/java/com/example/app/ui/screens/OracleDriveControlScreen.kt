package com.example.app.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.app.R
import dev.aurakai.auraframefx.ui.viewmodel.OracleDriveControlViewModel
import kotlinx.coroutines.launch

@Composable
fun OracleDriveControlScreen(
    viewModel: OracleDriveControlViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val isConnected by viewModel.isServiceConnected.collectAsState()
    val status by viewModel.status.collectAsState()
    val detailedStatus by viewModel.detailedStatus.collectAsState()
    val diagnosticsLog by viewModel.diagnosticsLog.collectAsState()
    var packageName by remember { mutableStateOf(TextFieldValue("")) }
    var enableModule by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val logScrollState = rememberScrollState()
    val viewModelScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.bindService()
        viewModel.refreshStatus()
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.unbindService() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = { /* Handle settings */ }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.settings)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(logScrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Connection status
            Text(
                text = if (isConnected) stringResource(R.string.oracle_drive_connected) 
                      else stringResource(R.string.oracle_drive_not_connected),
                style = MaterialTheme.typography.titleMedium,
                color = if (isConnected) MaterialTheme.colorScheme.primary 
                       else MaterialTheme.colorScheme.error
            )
            
            // Loading indicator
            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            // Error message
            errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Refresh button
            Button(
                onClick = { viewModelScope.launch { safeRefresh() } },
                enabled = isConnected && !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.refresh_status))
            }

            Divider()

            // Status information
            Text(stringResource(R.string.status_label, status ?: "-"))
            Text(stringResource(R.string.detailed_status_label, detailedStatus ?: "-"))
            
            // Diagnostics log
            Text(
                stringResource(R.string.diagnostics_log_label),
                style = MaterialTheme.typography.labelMedium
            )
            Box(
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = MaterialTheme.shapes.small
                    )
                    .padding(8.dp)
            ) {
                Text(
                    text = diagnosticsLog ?: "-",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.verticalScroll(logScrollState)
                )
            }

            Divider()

            // Module control
            Text(
                stringResource(R.string.toggle_module_label),
                style = MaterialTheme.typography.titleSmall
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = packageName,
                    onValueChange = { packageName = it },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    singleLine = true,
                    label = { Text(stringResource(R.string.module_package_name)) },
                    enabled = isConnected && !isLoading,
                    placeholder = { Text("com.example.app") }
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
}

private suspend fun OracleDriveControlScreenScope.safeRefresh() {
    isLoading = true
    errorMessage = null
    try {
        viewModel.refreshStatus()
    } catch (e: Exception) {
        errorMessage = "Failed to refresh: ${e.localizedMessage ?: e.toString()}"
    } finally {
        isLoading = false
    }
}

private suspend fun OracleDriveControlScreenScope.safeToggle() {
    if (packageName.text.isBlank()) return
    isLoading = true
    errorMessage = null
    try {
        viewModel.toggleModule(packageName.text, enableModule)
    } catch (e: Exception) {
        errorMessage = context.getString(R.string.failed_to_toggle, e.localizedMessage ?: e.toString())
    } finally {
        isLoading = false
    }
}

@Composable
private fun OracleDriveControlScreenScope.OracleDriveControlScreenScope() {
    // This scope provides access to the screen's state and viewModel
    // to the extension functions above
}
