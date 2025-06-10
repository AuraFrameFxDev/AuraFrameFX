package dev.aurakai.auraframefx.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.aurakai.auraframefx.model.AgentType
import dev.aurakai.auraframefx.ui.theme.NeonBlue
import dev.aurakai.auraframefx.ui.theme.NeonTeal
import dev.aurakai.auraframefx.viewmodel.ConferenceRoomViewModel

// Placeholder for RecordingButton - User should define this Composable
@Composable
fun RecordingButton(isRecording: Boolean, onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text(if (isRecording) "Stop Recording" else "Start Recording")
    }
}

// Placeholder for TranscribeButton - User should define this Composable
@Composable
fun TranscribeButton(isTranscribing: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = !isTranscribing
    ) { // Example: disable if already transcribing
        Text("Transcribe")
    }
}

// Placeholder for Header - User should define this Composable
@Composable
fun Header(selectedAgent: String, onAgentSelected: (String) -> Unit) {
    // Simplified placeholder
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Selected Agent: $selectedAgent", modifier = Modifier.padding(8.dp))
        Button(onClick = { onAgentSelected(if (selectedAgent == "Aura") "Kai" else "Aura") }) {
            Text("Switch Agent")
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConferenceRoomScreen(
    viewModel: ConferenceRoomViewModel = hiltViewModel(),
) {
    var selectedAgent by remember { mutableStateOf("Aura") } // Local state for agent selection UI
    val isRecording by viewModel.isRecording.collectAsState()
    val isTranscribing by viewModel.isTranscribing.collectAsState()
    val messages by viewModel.messages.collectAsState()
    var messageText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header and Agent Selection (Using placeholder Header)
        Header(selectedAgent = selectedAgent, onAgentSelected = { selectedAgent = it })

        // Recording Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            RecordingButton(
                isRecording = isRecording,
                onClick = { viewModel.toggleRecording() }
            )
            Spacer(modifier = Modifier.width(8.dp)) // Added spacer
            TranscribeButton(
                isTranscribing = isTranscribing,
                onClick = { viewModel.toggleTranscribing() }
            )
        }

        // Chat Interface
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            reverseLayout = true
        ) {
            items(messages.reversed()) { message -> // Reversed the list for typical chat display
                // Basic display for now, assuming AgentMessage has sender and content
                Text(
                    text = "[${message.sender}] ${message.content}",
                    color = Color.LightGray,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }

        // Input Area
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = messageText,
                onValueChange = { messageText = it },
                placeholder = { Text("Type your message...") },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                colors = TextFieldDefaults.colors(
                    // Updated to new M3 API if needed, or use M2 equivalent
                    focusedContainerColor = NeonTeal.copy(alpha = 0.1f),
                    unfocusedContainerColor = NeonTeal.copy(alpha = 0.1f),
                    // Ensure other color params are set if this causes issues with M3 defaults
                    // For M2: backgroundColor = NeonTeal.copy(alpha = 0.1f) might be used
                )
            )

            IconButton(
                onClick = {
                    if (messageText.isNotBlank()) {
                        viewModel.sendMessage(messageText, AgentType.USER) // Use AgentType.USER
                        messageText = ""
                    }
                }
            ) {
                Icon(
                    Icons.Default.Send,
                    contentDescription = "Send",
                    tint = NeonBlue
                )
            }
        }
    }
}
