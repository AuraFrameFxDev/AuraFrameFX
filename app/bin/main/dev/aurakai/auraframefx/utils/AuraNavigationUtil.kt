package dev.aurakai.auraframefx.utils

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import dev.aurakai.auraframefx.ai.NeuralWhisper
import timber.log.Timber

/**
 * Navigation utility for accessing Neural Whisper and other AI features
 */
object AuraNavigationUtil {

    /**
     * Navigate to the Neural Whisper feature
     *
     * @param activity The current activity
     * @param navController The navigation controller
     * @param triggerListening Whether to start listening immediately
     */
    fun navigateToNeuralWhisper(
        activity: FragmentActivity,
        navController: NavController,
        triggerListening: Boolean = false,
    ) {
        try {
            // Navigate to Neural Whisper fragment
            // In a real implementation, this would use proper navigation component
            // with a direction like: navController.navigate(R.id.action_to_neural_whisper)

            // For now we'll just log the intent
            Timber.d("Navigating to Neural Whisper feature")

            // If immediate listening requested, start it
            if (triggerListening) {
                val neuralWhisper = activity.application
                    .getSystemService("neural_whisper_service") as? NeuralWhisper

                neuralWhisper?.startListening()
            }
        } catch (e: Exception) {
            Timber.e(e, "Error navigating to Neural Whisper")
        }
    }

    /**
     * Toggle the ambient mood orb
     *
     * @param context The application context
     * @param show Whether to show or hide the orb
     */
    fun toggleAuraMoodOrb(context: Context, show: Boolean) {
        try {
            val neuralWhisper = context.applicationContext
                .getSystemService("neural_whisper_service") as? NeuralWhisper

            neuralWhisper?.toggleAmbientMood(show)
        } catch (e: Exception) {
            Timber.e(e, "Error toggling ambient mood orb: ${e.message}")
        }
    }
}
