package dev.aurakai.auraframefx.system.overlay.model

import android.graphics.Color

// Basic placeholder
// This would define properties for a shape, e.g., type (rectangle, circle, path), colors, corners, etc.
public data class OverlayShape(
    public val id: String = "default", // Added id as Impl uses it as map key
    public val shapeType: String = "rounded_rectangle", // e.g., "rounded_rectangle", "circle", "hexagon"
    public val fillColor: String? = null, // Changed to String for hex color codes
    public val strokeColor: String? = null, // Changed to String for hex color codes 
    public val strokeWidthPx: Float? = null,
    public val cornerRadius: Float? = null, // For rounded rectangles
    // val pathData: String? = null, // For custom paths
    public val shadow: OverlayShadow? = null // Added from previous theme files
)

// Copied from previous theme files, assuming it's related
public data class OverlayShadow(
    public val offsetX: Float = 0f,
    public val offsetY: Float = 0f,
    public val blurRadius: Float = 0f,
    public val color: String = "#33000000" // Changed to String for hex color codes
)
