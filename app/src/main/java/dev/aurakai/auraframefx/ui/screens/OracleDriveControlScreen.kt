package dev.aurakai.auraframefx.ui.screens

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
import dev.aurakai.auraframefx.R
import dev.aurakai.auraframefx.ui.viewmodel.OracleDriveControlViewModel
import kotlinx.coroutines.launch

@Composable
fun OracleDriveControlScreen(
    viewModel: OracleDriveControlViewModel = hiltViewModel(),
) {
    LocalContext.current
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
                    IconButton(onClick = { /* Open settings */ }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
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
                .verticalScroll(logScrollState)
        ) {
            // Connection status
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Connection Status: ${if (isConnected) "Connected" else "Disconnected"}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Status: $status")
                    Text("Details: $detailedStatus")
                }
            }


            Spacer(modifier = Modifier.height(16.dp))

            // Package control
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Package Control",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = packageName,
                        onValueChange = { packageName = it },
                        label = { Text("Package Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = {
                                viewModelScope.launch {
                                    try {
                                        isLoading = true
                                        errorMessage = null
                                        viewModel.toggleForPackage(packageName.text, enableModule)
                                    } catch (e: Exception) {
                                        errorMessage = e.message
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            },
                            enabled = !isLoading && packageName.text.isNotBlank()
                        ) {
                            Text(if (enableModule) "Enable" else "Disable")
                        }
                        Switch(
                            checked = enableModule,
                            onCheckedChange = { enableModule = it }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Diagnostics log
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Diagnostics Log",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = diagnosticsLog.ifEmpty { "No logs available" },
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }

    // Show loading indicator
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { /* Dismiss on click if needed */ },
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }

    // Show error message
    errorMessage?.let { message ->
        LaunchedEffect(message) {
            scaffoldState.snackbarHostState.showSnackbar(message)
            errorMessage = null
        }
    }
}

@Composable
fun OracleDriveControlScreenScope(
    viewModel: OracleDriveControlViewModel = hiltViewModel()
) {
    LocalContext.current
    
    fun safeRefresh() {
        viewModel.refreshStatus()
    }
    
    fun safeToggle(packageName: String, enable: Boolean) {
        viewModel.toggleForPackage(packageName, enable)
    }
}
