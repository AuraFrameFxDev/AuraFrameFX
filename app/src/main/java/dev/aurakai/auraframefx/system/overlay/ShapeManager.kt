package dev.aurakai.auraframefx.system.overlay

import android.graphics.Path
import dev.aurakai.auraframefx.system.overlay.model.OverlayShape

public class ShapeManager {
    
    /**
     * Creates a Path for the given shape configuration.
     * This method is referenced in QuickSettingsHooker for complex shapes like hexagons.
     */
    public fun createShapePath(shape: OverlayShape, width: Float, height: Float): Path {
        val path = Path()
        
        when (shape.shapeType.lowercase()) {
            "hexagon" -> {
                // Create hexagon path
                val centerX = width / 2f
                val centerY = height / 2f
                val radius = minOf(width, height) / 2f * 0.8f
                
                for (i in 0..5) {
                    val angle = Math.PI / 3 * i - Math.PI / 2
                    val x = centerX + radius * Math.cos(angle).toFloat()
                    val y = centerY + radius * Math.sin(angle).toFloat()
                    
                    if (i == 0) {
                        path.moveTo(x, y)
                    } else {
                        path.lineTo(x, y)
                    }
                }
                path.close()
            }
            "circle" -> {
                val centerX = width / 2f
                val centerY = height / 2f
                val radius = minOf(width, height) / 2f
                path.addCircle(centerX, centerY, radius, Path.Direction.CW)
            }
            "rectangle" -> {
                path.addRect(0f, 0f, width, height, Path.Direction.CW)
            }
            "rounded_rectangle" -> {
                val cornerRadius = shape.cornerRadius ?: 8f
                path.addRoundRect(0f, 0f, width, height, cornerRadius, cornerRadius, Path.Direction.CW)
            }
            else -> {
                // Default to rectangle
                path.addRect(0f, 0f, width, height, Path.Direction.CW)
            }
        }
        
        return path
    }
}

