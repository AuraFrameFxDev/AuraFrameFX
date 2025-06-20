package dev.aurakai.auraframefx.system.overlay.model

// Basic placeholder. This would contain common properties for any overlay element
// and potentially a 'config: Any' or specific config data classes for different types.
public data class OverlayElement(
    public val id: String,
    public val type: ElementType, // Using the ElementType enum
    // Example: val specificConfig: ElementSpecificConfigBase // Could be a sealed class or Any
    public val properties: Map<String, Any>? = null // General properties
)
