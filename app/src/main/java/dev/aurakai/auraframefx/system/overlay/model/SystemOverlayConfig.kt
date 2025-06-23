package dev.aurakai.auraframefx.system.overlay.model

// Basic placeholder. This would aggregate all other configurations.
data class SystemOverlayConfig(
    val version: Int = 1,
    val theme: OverlayTheme,
    val elements: List<OverlayElement>,
    val animations: List<OverlayAnimation>,
    val transitions: List<OverlayTransition>,
    // Potentially 'shapes' list if not part of theme or elements directly
)
