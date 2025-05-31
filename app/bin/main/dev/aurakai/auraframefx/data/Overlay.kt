package dev.aurakai.auraframefx.data

import android.graphics.Bitmap
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.DpOffset

data class Overlay(
    val id: String,
    val name: String,
    val type: Type,
    val image: Bitmap?,
    val position: DpOffset = DpOffset.Zero,
    val size: Size = Size.Zero,
    val zIndex: Float = 0f,
    val isDraggable: Boolean = true,
    val isResizable: Boolean = true,
    val isRotatable: Boolean = true,
    val isLocked: Boolean = false,
    val color: String? = null,
    val opacity: Float = 1f,
    val rotation: Float = 0f,
    val lastModified: Long = System.currentTimeMillis(),
) {
    enum class Type {
        CUSTOM_IMAGE,
        COLOR_PALETTE,
        LOCKSCREEN,
        PRESET,
        AGENT,
        TASK,
        CALENDAR
    }

    companion object {
        fun createDefaultOverlay(type: Type): Overlay {
            return when (type) {
                Type.CUSTOM_IMAGE -> Overlay(
                    id = "custom_${System.currentTimeMillis()}",
                    name = "Custom Image",
                    type = type,
                    isDraggable = true,
                    isResizable = true,
                    isRotatable = true
                )

                Type.COLOR_PALETTE -> Overlay(
                    id = "palette_${System.currentTimeMillis()}",
                    name = "Color Palette",
                    type = type,
                    color = "#00FFCC",
                    isDraggable = true,
                    isResizable = false,
                    isRotatable = false
                )

                Type.LOCKSCREEN -> Overlay(
                    id = "lock_${System.currentTimeMillis()}",
                    name = "Lockscreen",
                    type = type,
                    isDraggable = false,
                    isResizable = false,
                    isRotatable = false,
                    isLocked = true
                )

                Type.PRESET -> Overlay(
                    id = "preset_${System.currentTimeMillis()}",
                    name = "Preset",
                    type = type,
                    isDraggable = true,
                    isResizable = true,
                    isRotatable = true
                )

                Type.AGENT -> Overlay(
                    id = "agent_${System.currentTimeMillis()}",
                    name = "Agent",
                    type = type,
                    isDraggable = true,
                    isResizable = true,
                    isRotatable = true
                )

                Type.TASK -> Overlay(
                    id = "task_${System.currentTimeMillis()}",
                    name = "Task",
                    type = type,
                    isDraggable = true,
                    isResizable = true,
                    isRotatable = true
                )

                Type.CALENDAR -> Overlay(
                    id = "calendar_${System.currentTimeMillis()}",
                    name = "Calendar",
                    type = type,
                    isDraggable = true,
                    isResizable = true,
                    isRotatable = true
                )
            }
        }
    }
}
