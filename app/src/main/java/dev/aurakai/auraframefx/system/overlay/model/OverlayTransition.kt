package dev.aurakai.auraframefx.system.overlay.model

// Basic placeholder
public data class OverlayTransition(
    public val id: String, // Added id as Impl uses it as map key
    public val type: String, // e.g., "material_shared_axis", "fade_through"
    public val durationMs: Long? = null
)
