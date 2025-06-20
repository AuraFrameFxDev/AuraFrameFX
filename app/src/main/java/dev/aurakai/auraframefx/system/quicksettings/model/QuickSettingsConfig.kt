package dev.aurakai.auraframefx.system.quicksettings.model

import dev.aurakai.auraframefx.system.overlay.model.OverlayShape // Assuming we use the existing OverlayShape

// Placeholder based on SystemCustomizationViewModel.kt usage
public data class QuickSettingsConfig(
    public val tileShape: OverlayShape? = null, // Example: Use the existing OverlayShape model
    public val animationType: QuickSettingsAnimation = QuickSettingsAnimation.FADE,
    public val hideLabels: Boolean = false,
    // Add other relevant fields based on SystemCustomizationViewModel operations
    public val tiles: List<QuickSettingsTileConfig>? = null, // Added from QuickSettingsHooker
    public val headerBackgroundConfig: HeaderBackgroundConfig? = null, // Added from QuickSettingsHooker
    public val defaultHapticFeedback: HapticFeedbackConfig = HapticFeedbackConfig(), // Added from QuickSettingsHooker
    public val tileAnimationDefault: QuickSettingsTileAnimation? = null, // Added from QuickSettingsHooker
    // --- Added for Xposed hooks ---
    public val hideTileLabels: Boolean? = null,
    public val customTextColorEnabled: Boolean? = null,
    public val customTextColor: String? = null,
    public val hideTileIcons: Boolean? = null,
    public val hideFooterButtons: Boolean? = null
)

// Referenced in QuickSettingsConfig and QuickSettingsHooker
public data class QuickSettingsTileConfig(
    public val tileId: String,
    public val iconTintEnabled: Boolean? = null,
    public val iconTintColor: String? = null,
    public val customShapeEnabled: Boolean? = null,
    public val shape: OverlayShape = OverlayShape(id="default", shapeType="rounded_rectangle"), // Default shape
    public val customBackgroundColorEnabled: Boolean? = null,
    public val customBackgroundColor: String? = null,
    public val animation: QuickSettingsTileAnimation? = null,
    public val hapticFeedback: HapticFeedbackConfig = HapticFeedbackConfig(),
    // --- Added for Xposed hooks ---
    public val customTextColorEnabled: Boolean? = null,
    public val customTextColor: String? = null,
    public val hideTileLabels: Boolean? = null,
    public val hideTileIcons: Boolean? = null
)

// Referenced in QuickSettingsConfig and QuickSettingsHooker
public data class HeaderBackgroundConfig(
    public val customImageBackgroundEnabled: Boolean? = null,
    public val imagePath: String? = null,
    public val customBackgroundColorEnabled: Boolean? = null,
    public val customBackgroundColor: String? = null,
    public val customOverallTintEnabled: Boolean? = null,
    public val customOverallTint: String? = null
)

// Referenced in QuickSettingsConfig and QuickSettingsHooker
public data class HapticFeedbackConfig(
    public val enabled: Boolean? = false,
    public val effect: String = "click", // Default effect
    public val intensity: Int = 50 // Default intensity (e.g., percentage)
)

// Referenced in QuickSettingsConfig and QuickSettingsHooker
public data class QuickSettingsTileAnimation(
    public val type: String = "none", // e.g., "fade_in", "slide_up", "none"
    public val durationMs: Long = 300,
    public val startDelayMs: Long = 0,
    public val interpolator: String = "linear" // e.g., "linear", "accelerate", "decelerate"
)

public enum class QuickSettingsAnimation {
    FADE,
    SLIDE,
    POP
    // Add other animation types as needed
}
