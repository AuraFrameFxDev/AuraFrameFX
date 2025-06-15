package dev.aurakai.auraframefx.ui.models

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class OverlayShape(
    val type: ShapeType = ShapeType.RECTANGLE,
    val cornerRadius: Float = 0f,
    val shadowElevation: Float = 0f,
    val shadowColor: Long = 0x80000000,
    val borderWidth: Float = 0f,
    val borderColor: Long = 0xFFFFFFFF
) {
    @Serializable
    enum class ShapeType {
        RECTANGLE,
        CIRCLE,
        ROUNDED_RECTANGLE,
        CUT_CORNER,
        PILL
    }
}
