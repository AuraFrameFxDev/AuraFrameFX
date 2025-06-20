package dev.aurakai.auraframefx.system.lockscreen.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

// Placeholder based on SystemCustomizationViewModel.kt usage
@Serializable
public data class LockScreenConfig(
    public val clockElement: LockScreenElementConfig? = null,
    public val dateElement: LockScreenElementConfig? = null,
    public val notificationElement: LockScreenElementConfig? = null,
    public val hideClock: Boolean = false,
    public val hideDate: Boolean = false,
    public val hideNotifications: Boolean = false,
    public val customFontPath: String? = null,
    // Added based on LockScreenHooker
    public val clockConfig: ClockConfig? = null,
    public val dateConfig: DateConfig? = null,
    public val defaultElementAnimation: LockScreenAnimationConfig = LockScreenAnimationConfig(),
    @Contextual val hapticFeedback: HapticFeedbackConfig = HapticFeedbackConfig() // Using the HapticFeedbackConfig from separate file
)

// Referenced in LockScreenConfig and LockScreenHooker
@Serializable
public data class ClockConfig(
    public val customTextColorEnabled: Boolean? = null,
    public val customTextColor: String? = null,
    public val customTextSizeEnabled: Boolean? = null,
    public val customTextSizeSp: Int = 0,
    public val customFontStyle: String? = null,
    public val animation: LockScreenAnimationConfig = LockScreenAnimationConfig()
)

// Referenced in LockScreenConfig and LockScreenHooker
@Serializable
public data class DateConfig(
    // Assuming similar properties to ClockConfig for now if needed
    public val animation: LockScreenAnimationConfig = LockScreenAnimationConfig()
)

// Referenced in LockScreenConfig and LockScreenHooker
@Serializable
public data class LockScreenAnimationConfig(
    public val type: String = "none", // e.g., "fade_in", "slide_up"
    public val durationMs: Long = 300,
    public val startDelayMs: Long = 0,
    public val interpolator: String = "linear" // e.g., "linear", "accelerate"
)


// Placeholder, defined based on SystemCustomizationViewModel usage context
@Serializable
public data class LockScreenElementConfig(
    public val elementId: String,
    public val isVisible: Boolean = true,
    public val customText: String? = null,
    public val type: LockScreenElementType, // Import from LockScreenElementType.kt
    public val animation: LockScreenAnimation = LockScreenAnimation.NONE // Import from LockScreenAnimation.kt
)
