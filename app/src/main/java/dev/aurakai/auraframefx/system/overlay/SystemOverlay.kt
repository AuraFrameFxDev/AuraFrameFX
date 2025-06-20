package dev.aurakai.auraframefx.system.overlay // Ensure this package is correct

import kotlinx.serialization.Serializable

@Serializable
public data class SystemOverlayConfig(
    public val theme: OverlayTheme? = null,
    public val defaultAnimation: OverlayAnimation? = null,
    public val notchBar: NotchBarConfig = NotchBarConfig(),
    // New fields for this task:
    public val activeThemeName: String? = null,
    public val uiNetworkMode: String? = null,
    // Other existing SystemOverlayConfig fields should be preserved
)

// Added NotchBarConfig definition
@Serializable
public data class NotchBarConfig(
    public val enabled: Boolean = false,
    public val customBackgroundColorEnabled: Boolean = false,
    public val customBackgroundColor: String? = null, // Hex color string
    public val customImageBackgroundEnabled: Boolean = false,
    public val imagePath: String? = null, // Absolute path to the image file
    public val applySystemTransparency: Boolean = true,

    // NEW fields
    public val paddingTopPx: Int = 0,
    public val paddingBottomPx: Int = 0,
    public val paddingStartPx: Int = 0,
    public val paddingEndPx: Int = 0,
    public val marginTopPx: Int = 0,
    public val marginBottomPx: Int = 0,
    public val marginStartPx: Int = 0,
    public val marginEndPx: Int = 0,
    // TODO: Future: shape adjustments, content handling
)

@Serializable
public data class OverlayTheme(
    public val primaryColor: String = "#FFFFFF",
    public val secondaryColor: String = "#000000",
    public val accentColor: String = "#00BCD4",
    // Placeholder - user might have a more detailed OverlayTheme
    public val backgroundColor: String = "#FFFFFF", // Added for theme example
    public val isDarkTheme: Boolean = false, // Added for theme example
)

@Serializable
public data class OverlayElement(
    public val id: String,
    public val type: String, // e.g., "text", "image", "shape"
    public val shape: OverlayShape? = null,
    public val content: String? = null, // for text or image URI
    public val positionX: Int = 0,
    public val positionY: Int = 0,
    public val width: Int = 100,
    public val height: Int = 100,
)

@Serializable
public data class OverlayAnimation(
    public val type: String = "fade", // e.g., "fade", "slide_in_left"
    public val duration: Long = 300L,
    public val interpolator: String = "linear",
)

@Serializable
public data class OverlayTransition(
    public val type: String = "crossfade",
    public val duration: Long = 500L,
)

@Serializable
public data class OverlayShape(
    public val type: String = "rectangle", // e.g., "rectangle", "circle", "hexagon", "rounded_rectangle"
    public val cornerRadius: Float = 0f, // For rounded_rectangle
    public val sides: Int = 0, // For polygons like hexagon (6), triangle (3)
    public val rotationDegrees: Float = 0f, // For rotating the shape
    public val fillColor: String? = null, // Hex color for the shape's fill
    public val strokeColor: String? = null, // Hex color for the shape's border
    public val strokeWidthPx: Float = 0f, // Width of the border
    public val shadow: ShapeShadow? = null, // Optional shadow
    // TODO: Add properties for more complex shapes (e.g., path data)
)

@Serializable
public data class ShapeMargins(
    public val top: Int = 0,
    public val bottom: Int = 0,
    public val left: Int = 0,
    public val right: Int = 0,
)

@Serializable
public data class ShapePadding(
    public val top: Int = 0,
    public val bottom: Int = 0,
    public val left: Int = 0,
    public val right: Int = 0,
)

@Serializable
public data class ShapeBorder(
    public val color: String = "#FFFFFF",
    public val width: Int = 1,
    public val style: String = "solid",
)

@Serializable
public data class ShapeShadow(
    public val color: String? = null, // Hex color for shadow
    public val radius: Float = 0f, // Blur radius
    public val offsetX: Float = 0f,
    public val offsetY: Float = 0f,
)

@Serializable
public data class ShadowOffset(val x: Float = 0f, val y: Float = 2f)

// Enums like ElementType, AnimationType, TransitionType, ShapeType
// do not need @Serializable as per user instructions.
// If this file previously contained other classes or interfaces, they should be preserved.
