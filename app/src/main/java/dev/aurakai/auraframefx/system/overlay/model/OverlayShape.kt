package dev.aurakai.auraframefx.system.overlay.model

import androidx.compose.ui.graphics.Color

// Basic placeholder
// This would define properties for a shape, e.g., type (rectangle, circle, path), colors, corners, etc.
data class OverlayShape(
    val id: String, // Added id as Impl uses it as map key
    val shapeType: String, // e.g., "rounded_rectangle", "circle", "hexagon"
    val fillColor: Color? = null,
    val strokeColor: Color? = null,
    val strokeWidthPx: Float? = null,
    val cornerRadius: Float? = null, // For rounded rectangles
    // val pathData: String? = null, // For custom paths
    val shadow: OverlayShadow? = null, // Added from previous theme files
)

// Copied from previous theme files, assuming it's related
data class OverlayShadow(
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,
    val blurRadius: Float = 0f,
    val color: Color = Color.Black.copy(alpha = 0.2f),
)
