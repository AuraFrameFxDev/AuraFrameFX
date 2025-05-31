package dev.aurakai.auraframefx.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIChatScreen(onNavigateBack: () -> Unit) {
    var message by remember { mutableStateOf("") }
    var chatHistory by remember { mutableStateOf(listOf("Welcome to AI Chat!")) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000)),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            chatHistory.forEach {
                Text(
                    it,
                    color = Color.Cyan,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                label = { Text("Type a message", color = Color.Cyan) },
                modifier = Modifier.weight(1f),
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Cyan,
                    unfocusedBorderColor = Color.Magenta
                )
            )
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = {
                    if (message.isNotBlank()) {
                        chatHistory = chatHistory + "You: $message" + "\nAI: [response here]"
                        message = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan)
            ) {
                Text("Send", color = Color.Black)
            }
        }
        Button(
            onClick = onNavigateBack,
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Magenta)
        ) {
            Text("Back to Menu", color = Color.Black)
        }
    }
}
