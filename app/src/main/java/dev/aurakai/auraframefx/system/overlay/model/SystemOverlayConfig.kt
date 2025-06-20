package dev.aurakai.auraframefx.system.overlay.model

// Basic placeholder. This would aggregate all other configurations.
public data class SystemOverlayConfig(
    public val version: Int = 1,
    public val theme: OverlayTheme,
    public val elements: List<OverlayElement>,
    public val animations: List<OverlayAnimation>,
    public val transitions: List<OverlayTransition>
    // Potentially 'shapes' list if not part of theme or elements directly
)
