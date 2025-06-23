package dev.aurakai.auraframefx.system.lockscreen.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

// Placeholder based on SystemCustomizationViewModel.kt usage
@Serializable
data class LockScreenConfig(
    val clockElement: LockScreenElementConfig? = null,
    val dateElement: LockScreenElementConfig? = null,
    val notificationElement: LockScreenElementConfig? = null,
    val hideClock: Boolean = false,
    val hideDate: Boolean = false,
    val hideNotifications: Boolean = false,
    val customFontPath: String? = null,
    // Added based on LockScreenHooker
    val clockConfig: ClockConfig? = null,
    val dateConfig: DateConfig? = null,
    val defaultElementAnimation: LockScreenAnimationConfig = LockScreenAnimationConfig(),
    @Contextual val hapticFeedback: HapticFeedbackConfig = HapticFeedbackConfig(), // Using the HapticFeedbackConfig from separate file
)

// Referenced in LockScreenConfig and LockScreenHooker
@Serializable
data class ClockConfig(
    val customTextColorEnabled: Boolean? = null,
    val customTextColor: String? = null,
    val customTextSizeEnabled: Boolean? = null,
    val customTextSizeSp: Int = 0,
    val customFontStyle: String? = null,
    val animation: LockScreenAnimationConfig = LockScreenAnimationConfig(),
)

// Referenced in LockScreenConfig and LockScreenHooker
@Serializable
data class DateConfig(
    // Assuming similar properties to ClockConfig for now if needed
    val animation: LockScreenAnimationConfig = LockScreenAnimationConfig(),
)

// Referenced in LockScreenConfig and LockScreenHooker
@Serializable
data class LockScreenAnimationConfig(
    val type: String = "none", // e.g., "fade_in", "slide_up"
    val durationMs: Long = 300,
    val startDelayMs: Long = 0,
    val interpolator: String = "linear", // e.g., "linear", "accelerate"
)


// Placeholder, defined based on SystemCustomizationViewModel usage context
@Serializable
data class LockScreenElementConfig(
    val elementId: String,
    val isVisible: Boolean = true,
    val customText: String? = null,
    val type: LockScreenElementType, // Import from LockScreenElementType.kt
    val animation: LockScreenAnimation = LockScreenAnimation.NONE, // Import from LockScreenAnimation.kt
)
