package dev.aurakai.auraframefx.system.quicksettings.model

import dev.aurakai.auraframefx.system.overlay.model.OverlayShape // Assuming we use the existing OverlayShape

// Placeholder based on SystemCustomizationViewModel.kt usage
data class QuickSettingsConfig(
    val tileShape: OverlayShape? = null, // Example: Use the existing OverlayShape model
    val animationType: QuickSettingsAnimation = QuickSettingsAnimation.FADE,
    val hideLabels: Boolean = false,
    // Add other relevant fields based on SystemCustomizationViewModel operations
    val tiles: List<QuickSettingsTileConfig>? = null, // Added from QuickSettingsHooker
    val headerBackgroundConfig: HeaderBackgroundConfig? = null, // Added from QuickSettingsHooker
    val defaultHapticFeedback: HapticFeedbackConfig = HapticFeedbackConfig(), // Added from QuickSettingsHooker
    val tileAnimationDefault: QuickSettingsTileAnimation? = null, // Added from QuickSettingsHooker
    // --- Added for Xposed hooks ---
    val hideTileLabels: Boolean? = null,
    val customTextColorEnabled: Boolean? = null,
    val customTextColor: String? = null,
    val hideTileIcons: Boolean? = null,
    val hideFooterButtons: Boolean? = null,
)

// Referenced in QuickSettingsConfig and QuickSettingsHooker
data class QuickSettingsTileConfig(
    val tileId: String,
    val iconTintEnabled: Boolean? = null,
    val iconTintColor: String? = null,
    val customShapeEnabled: Boolean? = null,
    val shape: OverlayShape = OverlayShape(
        id = "default",
        shapeType = "rounded_rectangle"
    ), // Default shape
    val customBackgroundColorEnabled: Boolean? = null,
    val customBackgroundColor: String? = null,
    val animation: QuickSettingsTileAnimation? = null,
    val hapticFeedback: HapticFeedbackConfig = HapticFeedbackConfig(),
    // --- Added for Xposed hooks ---
    val customTextColorEnabled: Boolean? = null,
    val customTextColor: String? = null,
    val hideTileLabels: Boolean? = null,
    val hideTileIcons: Boolean? = null,
)

// Referenced in QuickSettingsConfig and QuickSettingsHooker
data class HeaderBackgroundConfig(
    val customImageBackgroundEnabled: Boolean? = null,
    val imagePath: String? = null,
    val customBackgroundColorEnabled: Boolean? = null,
    val customBackgroundColor: String? = null,
    val customOverallTintEnabled: Boolean? = null,
    val customOverallTint: String? = null,
)

// Referenced in QuickSettingsConfig and QuickSettingsHooker
data class HapticFeedbackConfig(
    val enabled: Boolean? = false,
    val effect: String = "click", // Default effect
    val intensity: Int = 50, // Default intensity (e.g., percentage)
)

// Referenced in QuickSettingsConfig and QuickSettingsHooker
data class QuickSettingsTileAnimation(
    val type: String = "none", // e.g., "fade_in", "slide_up", "none"
    val durationMs: Long = 300,
    val startDelayMs: Long = 0,
    val interpolator: String = "linear", // e.g., "linear", "accelerate", "decelerate"
)

enum class QuickSettingsAnimation {
    FADE,
    SLIDE,
    POP
    // Add other animation types as needed
}
