package dev.aurakai.auraframefx.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import dev.aurakai.auraframefx.ui.theme.NeonText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBuilderScreen(onNavigateBack: () -> Unit) {
    var idea by remember { mutableStateOf("") }
    var output by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        NeonText("App Builder", fontSize = 24.sp)
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(
            value = idea,
            onValueChange = { idea = it },
            label = { Text("Describe your app idea", color = Color.Yellow) },
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .border(width = 2.dp, color = Color.Yellow),
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Yellow,
                unfocusedBorderColor = Color.Magenta
            )
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { output = "[AI would generate: $idea]" },
            modifier = Modifier.fillMaxWidth(0.85f),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow)
        ) {
            Text("Generate", color = Color.Black)
        }
        Spacer(Modifier.height(24.dp))
        if (output.isNotBlank()) {
            Text(
                text = output,
                color = Color.Yellow,
                modifier = Modifier
                    .background(Color(0xFF000000))
                    .border(2.dp, Color.Magenta)
                    .padding(16.dp)
                    .fillMaxWidth(0.85f)
            )
        }
        Spacer(Modifier.height(40.dp))
        Button(
            onClick = onNavigateBack,
            modifier = Modifier.fillMaxWidth(0.5f),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Magenta)
        ) {
            Text("Back to Menu", color = Color.Black)
        }
    }
}
