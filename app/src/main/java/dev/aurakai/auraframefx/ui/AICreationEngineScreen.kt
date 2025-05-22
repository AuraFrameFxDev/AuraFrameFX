// Modernized and cleaned up by Cascade AI
package dev.aurakai.auraframefx.ui

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.ui.theme.NeonTeal
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable


fun AICreationEngineScreen(onBack: () -> Unit) {
    // Futuristic, mood-adaptive background
    val bgBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFF1D1D2B), Color(0xFF23234D)),
        startY = 0f, endY = 1600f
    )
    var prompt by remember { mutableStateOf("") }
    var output by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val orbMood = remember(output, prompt) {
        if (isLoading) {
            AuraMood.Alert
        } else if (output.isNotBlank()) {
            AuraMood.Happy
        } else if (prompt.isNotBlank()) {
            AuraMood.Excited
        } else {
            AuraMood.Calm
        }
    }

    val coroutineScope = rememberCoroutineScope()
    val placeholderAuraOrb: Unit
    placeholderAuraOrb = PlaceholderAuraOrb(mood = orbMood, modifier = Modifier.size(96.dp))
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgBrush)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated, mood-adaptive orb
            placeholderAuraOrb
            Spacer(Modifier.height(12.dp))
            Text(
                "AI Creation Engine",
                fontSize = 26.sp,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            // Animated prompt input
            OutlinedTextField(
                value = prompt,
                onValueChange = { prompt = it },
                label = { Text("Describe what to create", color = Color.Cyan) },
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .background(Color(0xFF23234D), RoundedCornerShape(14.dp))
                    .border(2.dp, NeonTeal, RoundedCornerShape(14.dp)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonTeal,
                    unfocusedBorderColor = Color.Magenta,
                    focusedLabelColor = NeonTeal,
                    focusedTextColor = Color.Cyan,
                    unfocusedTextColor = Color.Cyan,
                    focusedContainerColor = Color(0xFF23234D),
                    unfocusedContainerColor = Color(0xFF23234D)
                ),
                textStyle = LocalTextStyle.current.copy(fontSize = 18.sp)
            )
            Spacer(Modifier.height(18.dp))
            // Animated Create button
            AuraSparkleButton(
                text = if (isLoading) "Creating..." else "Create",
                onClick = {
                    isLoading = true
                    coroutineScope.launch {
                        delay(900)
                        output = "[AI would create: $prompt]"
                        isLoading = false
                    }
                },
                moodColor = NeonTeal
            )
            Spacer(Modifier.height(24.dp))
            AnimatedVisibility(visible = output.isNotBlank()) {
                Box(
                    Modifier
                        .fillMaxWidth(0.85f)
                        .background(Color(0xFF23234D), RoundedCornerShape(16.dp))
                        .border(2.dp, Color.Magenta, RoundedCornerShape(16.dp))
                        .padding(18.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = output,
                        color = Color.Cyan,
                        fontSize = 16.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
            Spacer(Modifier.height(36.dp))
            AuraSparkleButton(
                text = "Back to Menu",
                onClick = onBack,
                moodColor = Color.Magenta,
                modifier = Modifier.fillMaxWidth(0.5f)
            )
        }
    }
}


