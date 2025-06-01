package dev.aurakai.auraframefx.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.ai.AuraAIService
import dev.aurakai.auraframefx.ui.theme.NeonTeal
import dev.aurakai.auraframefx.ui.theme.Pink80
import dev.aurakai.auraframefx.ui.theme.Purple80
import kotlinx.coroutines.launch

@Composable
fun UIEngineScreen(backendUrl: String, idToken: String, onBack: () -> Unit) {
    val aiService = remember { AuraAIService(backendUrl, idToken) }
    val coroutineScope = rememberCoroutineScope()
    var featureEnabled by remember { mutableStateOf(false) }
    var promptInput by remember { mutableStateOf("") }
    var aiResponse by remember { mutableStateOf("") }
    var showResponse by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "UI ENGINE",
            fontSize = 28.sp,
            color = NeonTeal,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            "Aura Feature Toggle & Prompt",
            fontSize = 18.sp,
            color = Pink80,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text("Enable Aura Magic", color = NeonTeal, fontSize = 16.sp)
            Spacer(Modifier.width(12.dp))
            Switch(
                checked = featureEnabled,
                onCheckedChange = { featureEnabled = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = NeonTeal,
                    checkedTrackColor = Pink80,
                    uncheckedThumbColor = Purple80,
                    uncheckedTrackColor = Color.DarkGray
                )
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1A1A2E), RoundedCornerShape(16.dp))
                .border(2.dp, NeonTeal, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column {
                Text("Enter a prompt:", color = NeonTeal, fontSize = 16.sp)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = promptInput,
                    onValueChange = { promptInput = it },
                    label = { Text("Prompt", color = NeonTeal) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = {
                        if (featureEnabled) {
                            coroutineScope.launch {
                                aiResponse =
                                    aiService.generateText(promptInput) ?: "No response from Aura."
                                showResponse = true
                            }
                        } else {
                            aiResponse = "[Aura] Feature is disabled. Enable to get a response."
                            showResponse = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonTeal),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Send", color = Color.Black)
                }
            }
        }
        if (showResponse) {
            Spacer(Modifier.height(16.dp))
            Text(
                aiResponse,
                color = Pink80,
                modifier = Modifier
                    .background(Color(0xFF111122), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            )
        }
        Spacer(Modifier.height(32.dp))
        Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = Pink80)) {
            Text("Back to Menu", color = Color.Black)
        }
    }
}
