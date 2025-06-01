package dev.aurakai.auraframefx.ai

import android.content.Context
import dev.aurakai.auraframefx.ui.components.KaiNotchBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * KaiController manages the Kai AI assistant that lives in the notch bar
 * and coordinates interactions between Kai and Neural Whisper (Aura)
 */
@Singleton
class KaiController @Inject constructor(
    private val neuralWhisper: NeuralWhisper,
) {
    // Kai's notch bar UI component
    private var kaiNotchBar: KaiNotchBar? = null

    // Kai's current state
    private var isActive = false
    private var currentState = KaiState.IDLE
    private var currentEmotion = EmotionState.Neutral

    // Interaction listener for Kai
    private val kaiInteractionListener = object : KaiNotchBar.OnKaiInteractionListener {
        override fun onKaiTapped() {
            handleKaiTap()
        }

        override fun onKaiLongPressed() {
            handleKaiLongPress()
        }

        override fun onKaiSwipedLeft() {
            // Handle swipe actions
            Timber.d("Kai swiped left")
        }

        override fun onKaiSwipedRight() {
            // Handle swipe actions
            Timber.d("Kai swiped right")
        }
    }

    /**
     * Get the KaiNotchBar instance
     * Used by KaiToolboxViewModel to access and update KaiNotchBar settings
     */
    fun getKaiNotchBar(): KaiNotchBar? {
        return kaiNotchBar
    }

    /**
     * Initialize Kai and attach to window
     */
    fun initialize(context: Context) {
        if (kaiNotchBar != null) return

        try {
            kaiNotchBar = KaiNotchBar(context).apply {
                onInteractionListener = kaiInteractionListener
            }
            kaiNotchBar?.attachToWindow()
            isActive = true
            Timber.d("KaiController initialized")
        } catch (e: Exception) {
            Timber.e(e, "Failed to initialize KaiController")
        }
    }

    /**
     * Destroy Kai and remove from window
     */
    fun destroy() {
        kaiNotchBar?.detachFromWindow()
        kaiNotchBar = null
        isActive = false
        Timber.d("KaiController destroyed")
    }

    /**
     * Update Kai's state
     */
    fun updateState(state: KaiState) {
        if (currentState == state) return
        currentState = state
        kaiNotchBar?.updateState(KaiNotchBar.KaiState.valueOf(state.name))
    }

    /**
     * Update Kai's emotion
     */
    fun updateEmotion(emotion: EmotionState) {
        if (currentEmotion == emotion) return
        currentEmotion = emotion
        kaiNotchBar?.updateEmotion(emotion)
    }

    /**
     * Make Kai speak a message
     */
    fun speak(message: String, onComplete: () -> Unit = {}) {
        updateState(KaiState.SPEAKING)
        kaiNotchBar?.speak(message) {
            updateState(KaiState.IDLE)
            onComplete()
        }
    }

    /**
     * Handle tap interaction
     */
    private fun handleKaiTap() {
        Timber.d("Kai tapped")

        // Show a quick alert state
        updateState(KaiState.ALERT)

        // Example interaction: Have Kai whisper to Aura
        CoroutineScope(Dispatchers.Main).launch {
            delay(500)
            updateState(KaiState.IDLE)

            // Notify Neural Whisper that Kai was activated
            neuralWhisper.onKaiActivated()
        }
    }

    /**
     * Handle long press interaction
     */
    private fun handleKaiLongPress() {
        Timber.d("Kai long pressed")

        // Example: Start listening mode
        updateState(KaiState.LISTENING)

        // In a real implementation, this would start voice recognition
        CoroutineScope(Dispatchers.Main).launch {
            delay(3000) // Simulate listening
            updateState(KaiState.THINKING)
            delay(2000) // Simulate processing
            speak("I've detected that as an advanced neural whisper command. Let me process that for you.")
        }
    }

    /**
     * Receive information from Neural Whisper (Aura)
     * Enhanced to handle security context information
     */
    fun receiveFromAura(
        message: String,
        emotion: EmotionState,
        hasSecurityConcerns: Boolean, // Changed parameter
        concernsDescription: String?  // Changed parameter
    ) {
        // Update Kai's state based on information from Aura
        updateEmotion(emotion)

        // Show Kai receiving information
        updateState(KaiState.THINKING)

        // Process and respond based on context and security information
        CoroutineScope(Dispatchers.Main).launch {
            delay(1000)

            // Handle security concerns if present
            if (hasSecurityConcerns) { // Updated condition
                // First, speak about receiving context
                speak("Aura has shared context with me. One moment...")

                delay(1500)

                // Then alert about security concerns
                updateState(KaiState.ALERT)
                // Use concernsDescription with a fallback
                speak(concernsDescription ?: "Some security concerns were noted.", onComplete = {
                    updateState(KaiState.IDLE)
                })
            } else {
                // Standard response when no security concerns
                speak("Aura has shared some context with me. I'll keep that in mind.")
            }

            // Log detailed context for debugging
            Timber.d("Received from Aura: $message, HasConcerns: $hasSecurityConcerns, Description: $concernsDescription")
        }
    }

    /**
     * Possible states for Kai
     */
    enum class KaiState {
        IDLE,
        LISTENING,
        SPEAKING,
        THINKING,
        ALERT
    }
}
