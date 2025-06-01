package dev.aurakai.auraframefx.ui

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.aurakai.auraframefx.data.preferences.SecurePreferences
import dev.aurakai.auraframefx.ui.viewmodel.AIFeaturesViewModel
import dev.aurakai.auraframefx.ui.viewmodel.AIFeaturesViewModel.AIFeaturesUiState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.UUID
import javax.inject.Inject

/**
 * Data class to hold AI feature configuration
 */
data class AIConfig(
    val apiUrl: String,
    val apiKey: String,
    val isSecure: Boolean = true,
)

/**
 * Factory class to create AI configuration
 */
class AIConfigFactory @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    @Inject
    lateinit var securePreferences: SecurePreferences

    fun createConfig(): AIConfig {
        return AIConfig(
            apiUrl = BuildConfig.AI_API_URL.ifEmpty {
                // Fallback to secure preferences if BuildConfig is not set
                securePreferences.getString("ai_api_url", "") ?: ""
            },
            apiKey = securePreferences.getEncryptedString("ai_api_key") ?: "",
            isSecure = true
        )
    }
}

/**
 * Status card showing connection status and last update
 */
@Composable
private fun StatusCard(uiState: AIFeaturesUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            color = if (uiState.isConnected)
                                Color.Green else Color.Red,
                            shape = MaterialTheme.shapes.small
                        )
                )
                Text(
                    text = if (uiState.isConnected) "Connected" else "Disconnected",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            uiState.statusMessage?.let { message ->
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * Reusable card for feature sections
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FeatureCard(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        onClick = { /* Handle card click if needed */ },
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIFeaturesScreen(
    viewModel: AIFeaturesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    remember { SnackbarHostState() }

    // State for text inputs
    var promptText by rememberSaveable { mutableStateOf("") }
    var memoryKey by rememberSaveable { mutableStateOf("") }
    var memoryValue by rememberSaveable { mutableStateOf("") }
    var topicName by rememberSaveable { mutableStateOf("") }
    var messageText by rememberSaveable { mutableStateOf("") }
    var queryText by rememberSaveable { mutableStateOf("") }

    // Snackbar state
    val snackbarHostState = remember { SnackbarHostState() }
    var showAdvanced by remember { mutableStateOf(false) }

    // File picker launcher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { fileUri ->
            coroutineScope.launch {
                context.contentResolver.openInputStream(fileUri)?.use { inputStream ->
                    val fileName = fileUri.lastPathSegment ?: "file_${System.currentTimeMillis()}"
                    viewModel.uploadFile(fileName, inputStream.readBytes())
                }
            }
        }
    }

    // Handle side effects and events
    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is AIFeaturesViewModel.Event.ShowMessage -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Short
                    )
                }

                is AIFeaturesViewModel.Event.NavigateTo -> {
                    // Handle navigation if needed
                }
            }
        }
    }

    // Show error dialog if there's an error
    uiState.error?.let { error ->
        AlertDialog(
            onDismissRequest = { viewModel.updateStatus("") },
            title = { Text("Error") },
            text = { Text(error) },
            confirmButton = {
                Button(onClick = { viewModel.updateStatus("") }) {
                    Text("OK")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Aura & Kai AI Features") },
                actions = {
                    IconButton(onClick = { showAdvanced = !showAdvanced }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Advanced Settings"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            color = MaterialTheme.colorScheme.background
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Status Card
                item {
                    StatusCard(uiState)
                }

                // Text Generation Section
                item {
                    FeatureCard(
                        title = "Text Generation",
                        icon = Icons.Default.TextFields
                    ) {
                        OutlinedTextField(
                            value = promptText,
                            onValueChange = { promptText = it },
                            label = { Text("Enter your prompt") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                if (promptText.isNotBlank()) {
                                    coroutineScope.launch {
                                        viewModel.generateText(promptText)
                                    }
                                }
                            },
                            enabled = uiState.isConnected && !uiState.isLoading && promptText.isNotBlank(),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Generate Text")
                        }
                    }
                }

                // Memory Operations Section
                item {
                    FeatureCard(
                        title = "Memory Operations",
                        icon = Icons.Default.Memory
                    ) {
                        OutlinedTextField(
                            value = memoryKey,
                            onValueChange = { memoryKey = it },
                            label = { Text("Memory Key") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = memoryValue,
                            onValueChange = { memoryValue = it },
                            label = { Text("Memory Value") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (memoryKey.isNotBlank() && memoryValue.isNotBlank()) {
                                        coroutineScope.launch {
                                            viewModel.saveMemory(
                                                key = memoryKey,
                                                data = JSONObject(mapOf("value" to memoryValue))
                                            )
                                        }
                                    }
                                },
                                enabled = uiState.isConnected && !uiState.isLoading &&
                                        memoryKey.isNotBlank() && memoryValue.isNotBlank(),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Save")
                            }
                            Button(
                                onClick = {
                                    if (memoryKey.isNotBlank()) {
                                        coroutineScope.launch {
                                            viewModel.getMemory(memoryKey)
                                        }
                                    }
                                },
                                enabled = uiState.isConnected && !uiState.isLoading && memoryKey.isNotBlank(),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Retrieve")
                            }
                        }
                    }
                }

                // File Operations Section
                item {
                    FeatureCard(
                        title = "File Operations",
                        icon = Icons.Default.CloudUpload
                    ) {
                        Button(
                            onClick = { filePickerLauncher.launch("*/*") },
                            enabled = uiState.isConnected && !uiState.isLoading,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudUpload,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Upload File")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = "test-file-${UUID.randomUUID().toString().take(8)}.txt",
                            onValueChange = { /* Read-only */ },
                            label = { Text("File to download") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = false
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    viewModel.downloadFile("test-file.txt")
                                }
                            },
                            enabled = uiState.isConnected && !uiState.isLoading,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudDownload,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Download File")
                        }
                    }
                }

                // Advanced Features Section (conditionally shown)
                if (showAdvanced) {
                    // Analytics Query Section
                    item {
                        FeatureCard(
                            title = "Analytics Query",
                            icon = Icons.Default.DataExploration
                        ) {
                            OutlinedTextField(
                                value = queryText,
                                onValueChange = { queryText = it },
                                label = { Text("SQL Query") },
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 2
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        viewModel.runAnalyticsQuery(queryText)
                                    }
                                },
                                enabled = uiState.isConnected && !uiState.isLoading && queryText.isNotBlank(),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Run Query")
                            }
                        }
                    }

                    // Pub/Sub Section
                    item {
                        FeatureCard(
                            title = "Publish Message",
                            icon = Icons.Default.Publish
                        ) {
                            OutlinedTextField(
                                value = topicName,
                                onValueChange = { topicName = it },
                                label = { Text("Topic") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = messageText,
                                onValueChange = { messageText = it },
                                label = { Text("Message") },
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 2
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        viewModel.publishToPubSub(topicName, messageText)
                                    }
                                },
                                enabled = uiState.isConnected && !uiState.isLoading &&
                                        topicName.isNotBlank() && messageText.isNotBlank(),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                )
                            }
                        }
                    }
                }
