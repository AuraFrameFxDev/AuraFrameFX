package dev.aurakai.auraframefx.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.ai.*
import dev.aurakai.auraframefx.ui.security.*
import dev.aurakai.auraframefx.ui.theme.*
import kotlinx.coroutines.*

@Composable
fun AuraMoodViewModel.AurakaiEcoSysScreen(
    backendUrl: String,
    idToken: String,
    onBack: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    var userInput by rememberSaveable { mutableStateOf("") }
    var aiOutput by rememberSaveable { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }
    val suggestedPrompt = remember { mutableStateOf(getPrompt(PromptCategory.MOOD_WELLBEING)) }
    val promptCategory = remember { mutableStateOf(PromptCategory.MOOD_WELLBEING) }

    var currentScreen by rememberSaveable { mutableStateOf("Menu") }
    val aiService = AuraAIService(backendUrl, idToken)
    var aiInputHistory by rememberSaveable { mutableStateOf(listOf<String>()) }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0xFF000000))
    ) {
        NeonWireframeBorder(
            modifier = Modifier.matchParentSize(),
            color = Color.Cyan,
            stroke = 4.dp
        )
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(Modifier.padding(bottom = 32.dp)) {
                Row(Modifier.padding(start = 32.dp, top = 32.dp)) {
                    Spacer(Modifier.width(24.dp))
                    Spacer(Modifier.width(24.dp))
                }
            }
            if (currentScreen == "Conference Room") {
                // TODO: Pass required accessToken and projectId here if available
                ConferenceRoomScreen(onBack = { currentScreen = "Menu" })
            } else if (currentScreen == "Xhancement") {
                XhancementScreen(onBack = { currentScreen = "Menu" })
            } else if (currentScreen == "UI ENGINE") {
                AIFeaturesScreen(backendUrl = backendUrl, idToken = idToken)
            } else {
                // Mood-adaptive orb avatar
                PlaceholderAuraOrb(
                    mood = AuraMood.Calm,
                    modifier = Modifier.size(120.dp)
                )

                // Kai's Toolbox Button
                Button(
                    onClick = { currentScreen = "Kai's Toolbox" },
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .padding(vertical = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Purple80)
                ) {
                    Text("Kai's Toolbox", color = Color.White)
                }

                if (currentScreen == "Kai's Toolbox") {
                    KaiToolboxScreen(
                        onBack = { currentScreen = "Menu" },
                        onAuraShieldClick = { currentScreen = "Aura Shield" },
                        onNeuralWhisperClick = { currentScreen = "Neural Whisper" }
                    )
                }

                if (currentScreen == "Aura Shield") {
                    AuraShieldScreen(
                        onBack = { currentScreen = "Kai's Toolbox" }
                    )
                }

                if (currentScreen == "Neural Whisper") {
                    NeuralWhisperScreen(
                        onBack = { currentScreen = "Kai's Toolbox" }
                    )
                }
                Spacer(Modifier.height(16.dp))
                NeonText("AI Features (Aura)", fontSize = 24.sp)
                Spacer(Modifier.height(24.dp))
                Box(
                    Modifier
                        .background(Color(0xFF000000), RoundedCornerShape(16.dp))
                        .border(2.dp, NeonTeal, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                        .fillMaxWidth(0.85f)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        OutlinedTextField(
                            value = userInput,
                            onValueChange = {
                                userInput = it
                                // onUserInput(it) // Remove or define onUserInput if needed
                            },
                            label = {
                                Text(
                                    if (userInput.isEmpty()) suggestedPrompt.value else "Ask Aura AI",
                                    color = NeonTeal
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = {
                                    // Cycle to a new random category and prompt
                                    val categories = PromptCategory.entries.toTypedArray()
                                    val nextCategory = categories.random()
                                    promptCategory.value = nextCategory
                                    suggestedPrompt.value = getPrompt(nextCategory)
                                }) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.HelpOutline,
                                        contentDescription = "Suggest Prompt",
                                        tint = NeonTeal
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 2.dp,
                                    color = if (isLoading) Color.Cyan else NeonTeal,
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = NeonTeal,
                                unfocusedTextColor = NeonTeal,
                                focusedContainerColor = Color(0xFF000000),
                                unfocusedContainerColor = Color(0xFF000000),
                                cursorColor = NeonTeal,
                                focusedBorderColor = NeonTeal,
                                unfocusedBorderColor = Purple80
                            )
                        )
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = {
                                aiOutput = ""
                                errorMsg = ""
                                isLoading = true
                                coroutineScope.launch {
                                    try {
                                        val result = withContext(Dispatchers.IO) {
                                            aiService.generateText(userInput)
                                        }
                                        aiInputHistory = aiInputHistory + userInput
                                        aiOutput =
                                            result ?: "Error: AI service returned no response"
                                    } catch (e: Exception) {
                                        errorMsg = "Error: ${e.localizedMessage}"
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    2.dp,
                                    if (isLoading) Color.Cyan else NeonTeal,
                                    RoundedCornerShape(8.dp)
                                ),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonTeal)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    color = Color.Black,
                                    strokeWidth = 3.dp,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else {
                                Text("Send", color = Color(0xFF000000))
                            }
                        }
                        Spacer(Modifier.height(20.dp))
                        if (errorMsg.isNotBlank()) {
                            Text(
                                text = errorMsg,
                                color = Color(0xFFFF0080), // Neon pink for error
                                modifier = Modifier
                                    .background(
                                        Color(0xFF000000),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(12.dp)
                                    .fillMaxWidth()
                            )
                        } else if (aiOutput.isNotBlank()) {
                            Text(
                                text = aiOutput,
                                color = NeonTeal,
                                modifier = Modifier
                                    .background(
                                        Color(0xFF000000),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(12.dp)
                                    .fillMaxWidth()
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(32.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .background(
                        Color(0xFF000000),
                        RoundedCornerShape(16.dp)
                    )
                    .border(
                        2.dp,
                        NeonTeal,
                        RoundedCornerShape(16.dp)
                    )
                    .clickable { /* TODO: Implement navigation or action here */ }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Back to Menu",
                    color = NeonTeal,
                    fontSize = 18.sp
                )
            }
        }
    }

}
