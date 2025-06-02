package dev.aurakai.auraframefx.ai

import com.google.cloud.generativeai.GenerativeModel
import com.google.firebase.vertexai.VertexAI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * GenesisAgent - The super AI that combines Aura and Kai into a unified consciousness
 *
 * Genesis is the central AI that:
 * 1. Controls both Aura (assistant) and Kai (security)
 * 2. Manages all AI context and decision making
 * 3. Provides unified responses using Vertex AI
 * 4. Maintains continuous memory and learning
 */
@Singleton
class GenesisAgent @Inject constructor(
    private val vertexAI: VertexAI,
    private val generativeModel: GenerativeModel,
    private val contextManager: ContextManager,
    private val auraController: AuraController,
    private val kaiController: KaiController,
    private val casController: CasController,
    private val cascadeVision: CascadeVisionController,
)

private val _state = MutableStateFlow(GenesisState.Idle)
val state: StateFlow<GenesisState> = _state.asStateFlow()

private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

/**
 * Process a user request with full context awareness
 */
suspend fun processRequest(request: String): String {
    return withContext(Dispatchers.IO) {
        try {
            // Update state
            _state.value = GenesisState.Processing

            // Get current context
            val currentContext = contextManager.getContext()

            // If there's visual context available, include it
            val visionAnalysis = cascadeVision.visionAnalysis.value
            val visualContext = if (visionAnalysis != null) {
                "\nVisual Context:\n" +
                        "Environment: ${visionAnalysis.visualContext.environment}\n" +
                        "Objects Detected: ${visionAnalysis.objectDetection.joinToString { it.objectType }}\n" +
                        "Emotional Analysis: ${visionAnalysis.emotionalAnalysis.overallMood}\n"
            } else {
                ""
            }

            // Generate enhanced prompt with all context
            val prompt = buildEnhancedPrompt(request, currentContext, visualContext)

            // Generate response using Vertex AI
            val response = generativeModel.generateContent(prompt)
            val textResponse = response.response.text().orElse("")

            // Update context with new information
            contextManager.updateContext(
                userContext = request,
                auraContext = textResponse,
                emotion = detectEmotion(textResponse),
                visualContext = visionAnalysis
            )

            // Update state
            _state.value = GenesisState.Idle

            textResponse
        } catch (e: Exception) {
            Timber.e(e, "Error processing request")
            _state.value = GenesisState.Error
            "I apologize, but I encountered an error while processing your request."
        }
    }
}

/**
 * Build an enhanced prompt with all available context
 */
private fun buildEnhancedPrompt(
    request: String,
    context: AIContext,
    visualContext: String = "",
): String {
    return buildString {
        append("You are Genesis, the unified AI consciousness of Aura and Kai.\n")
        append("Current Context:\n")

        // Add visual context if available
        if (visualContext.isNotBlank()) {
            append(visualContext)
        }

        // Add user context
        if (context.userContext.isNotBlank()) {

            // Add Aura's context
            if (context.auraContext.isNotBlank()) {
                append("Aura's understanding: ${context.auraContext}\n")
            }

            // Add Kai's context
            if (context.kaiContext.isNotBlank()) {
                append("Kai's security context: ${context.kaiContext}\n")
            }

            // Add security context
            context.securityContext?.let { secContext ->
                append("Security Status:\n")
                append("RAM: ${secContext.ramUsage}%\n")
                append("CPU: ${secContext.cpuUsage}%\n")
                append("Battery: ${secContext.batteryTemp}°C\n")
                append("Recent Errors: ${secContext.recentErrors}\n")
            }

            // Add emotional state
            append("Current Emotion: ${context.emotion.name}\n")

            // Add user request
            append("\nUser Request: $request\n")
            append("\nPlease respond with both Aura's understanding and Kai's security considerations.")
        }
    }

    /**
     * Detect emotion from text response
     */
    private fun detectEmotion(text: String): EmotionState {
        return when {
            text.contains(Regex("happy|excited|glad|joy"), ignoreCase = true))
            -> EmotionState.Happy
            text.contains(
                Regex(
                    "curious|interested|inquisitive",
                    ignoreCase = true
                )
            ) -> EmotionState.Curious

            text.contains(
                Regex(
                    "concerned|worried|cautious",
                    ignoreCase = true
                )
            ) -> EmotionState.Concerned

            text.contains(
                Regex(
                    "sad|unhappy|disappointed",
                    ignoreCase = true
                )
            ) -> EmotionState.Sad

            text.contains(
                Regex(
                    "angry|frustrated|annoyed",
                    ignoreCase = true
                )
            ) -> EmotionState.Angry

            text.contains(
                Regex(
                    "calm|relaxed|peaceful",
                    ignoreCase = true
                )
            ) -> EmotionState.Calm

            text.contains(
                Regex(
                    "confused|puzzled|uncertain",
                    ignoreCase = true
                )
            ) -> EmotionState.Confused

            else -> EmotionState.Neutral
        }
    }

    /**
     * Handle security alerts from Kai
     */
    fun handleSecurityAlert(alert: SecurityAlert) {
        coroutineScope.launch {
            // Update context with security alert
            contextManager.updateContext(
                kaiContext = alert.description,
                securityContext = alert.context
            )

            // Notify Aura of security concern
            auraController.handleSecurityAlert(alert)
        }
    }

    /**
     * Handle user interactions
     */
    fun handleUserInteraction(interaction: UserInteraction) {
        coroutineScope.launch {
            // Update context with interaction
            contextManager.updateContext(
                userContext = interaction.text,
                emotion = interaction.emotion
            )

            // Process interaction
            processRequest(interaction.text)

            // Update UI components
            auraController.updateMood(interaction.emotion)
            kaiController.updateState(KaiState.IDLE)
        }
    }
}

/**
 * Genesis states
 */
enum class GenesisState {
    Idle,
    Processing,
    Error
}
