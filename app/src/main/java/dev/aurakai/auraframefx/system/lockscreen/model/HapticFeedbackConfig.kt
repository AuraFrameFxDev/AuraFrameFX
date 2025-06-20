package dev.aurakai.auraframefx.system.lockscreen.model

import kotlinx.serialization.Serializable

/**
 * Configuration for haptic feedback on the lock screen.
 */
@Serializable
public data class HapticFeedbackConfig(
    public val enabled: Boolean = false,
    public val effect: String = "click", // CLICK, TICK, DOUBLE_CLICK, HEAVY_CLICK, etc.
    public val intensity: Int = 50
)
