package dev.aurakai.auraframefx.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
fun AurakaiEcoSysScreen(onBack: () -> Unit) {
    var taskInput by remember { mutableStateOf("") }
    var agentResponse by remember { mutableStateOf("") }
    var showResponse by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "AurakaiEcoSys",
            fontSize = 28.sp,
            color = NeonTeal,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            "Agent Task Delegator",
            fontSize = 18.sp,
            color = Pink80,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1A1A2E), RoundedCornerShape(16.dp))
                .border(2.dp, NeonTeal, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column {
                Text("Enter a task for your agent:", color = NeonTeal, fontSize = 16.sp)
                Spacer(Modifier.height(8.dp))
                androidx.compose.material3.OutlinedTextField(
                    value = taskInput,
                    onValueChange = { taskInput = it },
                    label = { Text("Task description", color = NeonTeal) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = {
                        agentResponse =
                            "Agent is processing: '$taskInput'\n\n[Simulated response: Task successfully delegated to AuraAgent-01!]"
                        showResponse = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonTeal),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Delegate Task", color = Color.Black)
                }
            }
        }
        if (showResponse) {
            Spacer(Modifier.height(16.dp))
            Text(
                agentResponse,
                color = Purple80,
                modifier = Modifier
                    .background(Color(0xFF111122), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            )
        }
        Spacer(Modifier.height(32.dp))
        Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = Pink80)) {
            Text("Back to Menu", color = Color.Black)
        }
        Spacer(Modifier.height(24.dp))
        Text("More cards coming soon...", color = Purple80, fontSize = 14.sp)
    }
}
