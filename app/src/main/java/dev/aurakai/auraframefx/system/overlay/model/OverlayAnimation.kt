package dev.aurakai.auraframefx.system.overlay.model

// Basic placeholder
public data class OverlayAnimation(
    public val id: String, // Added id as Impl uses it as map key
    public val type: String, // e.g., "fade_in", "slide_up"
    public val durationMs: Long? = null,
    public val targetProperty: String? = null // e.g., "alpha", "translationY"
)
