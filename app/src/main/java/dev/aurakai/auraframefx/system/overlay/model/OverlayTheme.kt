package dev.aurakai.auraframefx.system.overlay.model

import androidx.compose.ui.graphics.Color // Assuming colors will be Compose Colors

// Basic placeholder. The Impl used theme.colors, theme.fonts, theme.shapes
// These would be maps or lists of more specific types.
public data class OverlayTheme(
    public val name: String,
    public val colors: Map<String, Color>? = null, // e.g., "primary" to Color(0xFFFFFFFF)
    public val fonts: Map<String, String>? = null,   // e.g., "body" to "font_family_name"
    public val shapes: Map<String, OverlayShape>? = null // e.g., "button" to an OverlayShape definition
)
