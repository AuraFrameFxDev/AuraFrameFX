package dev.aurakai.auraframefx.ui.models

import androidx.compose.animation.core.*
import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

// Import the RepeatMode from our own package
import dev.aurakai.auraframefx.ui.models.RepeatMode

@Immutable
@Serializable
data class QuickSettingsAnimation(
    val type: AnimationType = AnimationType.NONE,
    val duration: Int = 300,
    val delay: Int = 0,
    val repeatMode: RepeatMode = RepeatMode.RESTART,
    val repeatCount: Int = 0,
    val interpolator: Interpolator = Interpolator.LINEAR,
    val amplitude: Float = 0.1f,
    val frequency: Float = 2f
) {
    @Serializable
    enum class AnimationType {
        NONE,
        FADE,
        SCALE,
        SLIDE_UP,
        SLIDE_DOWN,
        SLIDE_LEFT,
        SLIDE_RIGHT,
        ROTATE,
        BOUNCE,
        PULSE,
        JITTER
    }

    @Serializable
    enum class Interpolator {
        LINEAR,
        FAST_OUT_SLOW_IN,
        FAST_OUT_LINEAR_IN,
        LINEAR_OUT_SLOW_IN,
        EASE_IN,
        EASE_OUT,
        EASE_IN_OUT
    }
}
