package dev.aurakai.auraframefx.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.ui.theme.NeonTeal
import dev.aurakai.auraframefx.ui.theme.Pink80
import dev.aurakai.auraframefx.ui.theme.Purple80

@Composable
fun ConferenceRoomScreen(onBack: () -> Unit) {
    var userInput by remember { mutableStateOf("") }
    var chatLog by remember { mutableStateOf(listOf("[AuraAgent-01] Welcome to the Conference Room!")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Conference Room",
            fontSize = 28.sp,
            color = NeonTeal,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            "Multi-Agent Chat",
            fontSize = 18.sp,
            color = Pink80,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(0xFF1A1A2E), RoundedCornerShape(16.dp))
                .border(2.dp, NeonTeal, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Bottom
            ) {
                chatLog.forEach { msg ->
                    Text(
                        msg,
                        color = Purple80,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = userInput,
            onValueChange = { userInput = it },
            label = { Text("Type your message", color = NeonTeal) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = {
                if (userInput.isNotBlank()) {
                    chatLog = chatLog + "[You] $userInput"
                    // Simulate AI agent response
                    chatLog = chatLog + "[AuraAgent-01] Received: '$userInput'"
                    userInput = ""
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = NeonTeal),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Send", color = Color.Black)
        }
        Spacer(Modifier.height(16.dp))
        Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = Pink80)) {
            Text("Back to Menu", color = Color.Black)
        }
    }
}
