package com.example.app.system.overlay

import com.example.app.ui.theme.Color
import kotlinx.serialization.Serializable

@Serializable
data class SystemOverlayConfig(
    val theme: OverlayTheme,
    val elements: List<OverlayElement>,
    val animations: List<OverlayAnimation>,
    val transitions: List<OverlayTransition>,
)

@Serializable
data class OverlayTheme(
    val name: String,
    val colors: Map<String, Color>,
    val fonts: Map<String, String>,
    val shapes: Map<String, OverlayShape>,
)

@Serializable
data class OverlayElement(
    val id: String,
    val type: ElementType,
    val target: String,
    val properties: Map<String, Any>,
    val animations: List<OverlayAnimation>,
)

@Serializable
data class OverlayAnimation(
    val id: String,
    val type: AnimationType,
    val duration: Long,
    val easing: String,
    val properties: Map<String, Any>,
)

@Serializable
data class OverlayTransition(
    val id: String,
    val type: TransitionType,
    val duration: Long,
    val easing: String,
    val from: Map<String, Any>,
    val to: Map<String, Any>,
)

@Serializable
data class OverlayShape(
    val id: String,
    val type: ShapeType,
    val properties: Map<String, Any>,
    val margins: ShapeMargins = ShapeMargins(),
    val padding: ShapePadding = ShapePadding(),
    val border: ShapeBorder = ShapeBorder(),
    val shadow: ShapeShadow = ShapeShadow(),
)

@Serializable
data class ShapeMargins(
    val left: Int = 0,
    val top: Int = 0,
    val right: Int = 0,
    val bottom: Int = 0,
)

@Serializable
data class ShapePadding(
    val left: Int = 0,
    val top: Int = 0,
    val right: Int = 0,
    val bottom: Int = 0,
)

@Serializable
data class ShapeBorder(
    val width: Int = 0,
    val color: String = "#000000",
    val radius: Float = 0f,
    val corners: Map<String, Float> = mapOf(
        "topLeft" to 0f,
        "topRight" to 0f,
        "bottomLeft" to 0f,
        "bottomRight" to 0f
    ),
)

@Serializable
data class ShapeShadow(
    val color: String = "#000000",
    val offset: ShadowOffset = ShadowOffset(),
    val blurRadius: Float = 0f,
    val spreadRadius: Float = 0f,
)

@Serializable
data class ShadowOffset(
    val x: Float = 0f,
    val y: Float = 0f,
)

enum class ElementType {
    QUICK_SETTINGS,
    LOCK_SCREEN,
    NOTIFICATION,
    STATUS_BAR,
    APP_DRAWER,
    LAUNCHER,
    SYSTEM_UI,
    APP_OVERLAY
}

enum class AnimationType {
    FADE,
    SCALE,
    ROTATE,
    SLIDE,
    PULSE,
    GLOW
}

enum class TransitionType {
    SLIDE,
    FADE,
    SCALE,
    ROTATE,
    PULSE,
    GLOW
}

enum class ShapeType {
    RECTANGLE,
    ROUND_RECT,
    CIRCLE,
    ARC,
    WAVE,
    CUSTOM
}

interface SystemOverlayManager {
    fun applyTheme(theme: OverlayTheme)
    fun applyElement(element: OverlayElement)
    fun applyAnimation(animation: OverlayAnimation)
    fun applyTransition(transition: OverlayTransition)
    fun applyShape(shape: OverlayShape)
    fun applyConfig(config: SystemOverlayConfig)
    fun removeElement(elementId: String)
    fun clearAll()
}

interface SystemOverlayService {
    fun startOverlay()
    fun stopOverlay()
    fun updateOverlay(config: SystemOverlayConfig)
    fun isOverlayActive(): Boolean
    fun getActiveConfig(): SystemOverlayConfig?
}
