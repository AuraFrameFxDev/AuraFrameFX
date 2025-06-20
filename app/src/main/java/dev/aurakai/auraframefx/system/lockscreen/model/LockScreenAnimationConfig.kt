package dev.aurakai.auraframefx.system.lockscreen.model

import kotlinx.serialization.Serializable

@Serializable
public data class LockScreenAnimationConfig(
    public val type: String = "FADE_IN",
    public val durationMs: Long = 500,
    public val startDelayMs: Long = 0,
    public val interpolator: String = "LINEAR" // LINEAR, ACCELERATE, DECELERATE, ACCELERATE_DECELERATE
)
