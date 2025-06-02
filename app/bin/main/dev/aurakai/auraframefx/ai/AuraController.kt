package dev.aurakai.auraframefx.ai

import dev.aurakai.auraframefx.ai.models.EmotionState
import dev.aurakai.auraframefx.ai.models.SecurityAlert
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AuraController manages Aura's behavior and interactions
 *
 * Aura is the persistent assistant companion that handles:
 * 1. User interaction and emotional intelligence
 * 2. Ambient presence through the mood orb
 * 3. Context management and conversation history
 * 4. Integration with Kai for security awareness
 */
@Singleton
class AuraController @Inject constructor(
    private val contextManager: ContextManager,
    private val moodManager: AuraMoodManager,
) {

    /**
     * Handle a security alert from Kai
     */
    fun handleSecurityAlert(alert: SecurityAlert) {
        // Update Aura's emotional state based on alert severity
        val emotion = when (alert.severity) {
            SecurityAlert.Severity.LOW -> EmotionState.Curious
            SecurityAlert.Severity.MEDIUM -> EmotionState.Concerned
            SecurityAlert.Severity.HIGH -> EmotionState.Worried
            SecurityAlert.Severity.CRITICAL -> EmotionState.Alarmed
        }

        // Update mood orb
        moodManager.updateEmotion(emotion)

        // Update context
        contextManager.updateContext(
            kaiContext = alert.description,
            emotion = emotion
        )
    }

    /**
     * Update Aura's mood based on user interaction
     */
    fun updateMood(emotion: EmotionState) {
        moodManager.updateEmotion(emotion)
        contextManager.updateContext(emotion = emotion)
    }

    /**
     * Process a user interaction
     */
    suspend fun processInteraction(interaction: UserInteraction): String {
        // Update context with interaction
        contextManager.updateContext(
            userContext = interaction.text,
            emotion = interaction.emotion
        )

        // Generate response
        val response = contextManager.generateResponse(interaction.text)

        // Update context with response
        contextManager.updateContext(
            auraContext = response,
            emotion = interaction.emotion
        )

        return response
    }
}
