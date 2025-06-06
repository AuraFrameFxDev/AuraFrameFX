package com.example.app.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AiChatScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        // Message list area
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            // Example messages - replace with actual data handling
            item { Text("User: Hello AI!", style = MaterialTheme.typography.bodyMedium) }
            item { Spacer(modifier = Modifier.height(4.dp)) }
            item {
                Text(
                    "AI: Hi there, User! How can I help you today?",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            item { Spacer(modifier = Modifier.height(4.dp)) }
            item { Text("User: Tell me a fun fact.", style = MaterialTheme.typography.bodyMedium) }
            item { Spacer(modifier = Modifier.height(4.dp)) }
            item {
                Text(
                    "AI: Honey never spoils. Archaeologists have found pots of honey in ancient Egyptian tombs that are over 3,000 years old and still perfectly edible!",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        // Input area
        var textState by remember { mutableStateOf("") }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextField(
                value = textState,
                onValueChange = { textState = it },
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, Color.Gray, MaterialTheme.shapes.small),
                placeholder = { Text("Type your message...") }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { /* TODO: Handle send action */ }) {
                Text("Send")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AiChatScreenPreview() {
    MaterialTheme { // Using MaterialTheme for preview
        AiChatScreen()
    }
}
