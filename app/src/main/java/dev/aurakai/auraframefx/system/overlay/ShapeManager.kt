package dev.aurakai.auraframefx.system.overlay

import android.graphics.Path
import android.graphics.RectF
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.cos // For hexagon calculation
import kotlin.math.sin // For hexagon calculation

@Singleton
class ShapeManager @Inject constructor() {

    private val TAG = "ShapeManager" // Optional: for logging if needed later

    /**
     * Generates an Android Path object based on the OverlayShape configuration.
     * The Path can then be used to draw the shape on a Canvas.
     * @param shapeConfig The configuration of the shape.
     * @param bounds The RectF defining the area within which the shape should be drawn.
     * @return A Path object representing the custom shape.
     */
    fun createShapePath(shapeConfig: OverlayShape, bounds: RectF): Path {
        val path = Path()
        when (shapeConfig.type.lowercase()) { // Use lowercase for case-insensitivity
            "rectangle" -> path.addRect(bounds, Path.Direction.CW)
            "rounded_rectangle" -> {
                val cornerRadius = shapeConfig.cornerRadius.coerceAtLeast(0f)
                path.addRoundRect(bounds, cornerRadius, cornerRadius, Path.Direction.CW)
            }

            "circle" -> {
                val radius = bounds.width().coerceAtMost(bounds.height()) / 2f
                if (radius > 0) {
                    path.addCircle(bounds.centerX(), bounds.centerY(), radius, Path.Direction.CW)
                } else {
                    // Fallback for zero or negative radius if bounds are invalid
                    path.addRect(bounds, Path.Direction.CW)
                }
            }

            "hexagon" -> {
                // Ensure sides is 6 for hexagon, or use shapeConfig.sides if flexible polygons are desired
                val sides = 6 // For a hexagon
                if (sides >= 3) {
                    val centerX = bounds.centerX()
                    val centerY = bounds.centerY()
                    // Use the smaller of width/height to determine radius to fit within bounds
                    val radius = bounds.width().coerceAtMost(bounds.height()) / 2f
                    if (radius > 0) {
                        path.reset() // Clear path before adding points
                        for (i in 0 until sides) {
                            // Angle adjustment (-90 degrees or PI/2) to make hexagon flat top/bottom
                            // Or -30 degrees (PI/6) for point top/bottom if desired
                            // For flat top, initial angle for first point could be -Math.PI / sides (e.g., -PI/6 for hexagon makes it pointy top)
                            // Let's try for flat top: (2 * Math.PI * i / sides) - (Math.PI / 2)
                            // User's example: (60 * i - 30) for flat top - (Math.PI / 3.0 * i - Math.PI / 6.0)
                            // Let's use a common flat-top orientation: start angle PI/6 (30 deg) from positive X-axis
                            val angleRad =
                                (2.0 * Math.PI * i / sides) + (Math.PI / 6.0) // Rotated for flat top

                            val x = centerX + (radius * cos(angleRad)).toFloat()
                            val y = centerY + (radius * sin(angleRad)).toFloat()
                            if (i == 0) {
                                path.moveTo(x, y)
                            } else {
                                path.lineTo(x, y)
                            }
                        }
                        path.close()
                    } else {
                        path.addRect(bounds, Path.Direction.CW) // Fallback for zero radius
                    }
                } else {
                    path.addRect(bounds, Path.Direction.CW) // Fallback for invalid sides
                }
            }
            // TODO: Add more shape types as needed (e.g., "triangle", "star", "custom_path_data")
            else -> {
                // Log.w(TAG, "Unsupported shape type: ${shapeConfig.type}. Defaulting to rectangle.")
                path.addRect(bounds, Path.Direction.CW) // Default to rectangle
            }
        }
        // path.close() // Closing might be redundant if shapes like addRect already close or if path is filled
        // For addRoundRect, addCircle, addRect, close() is implicit. For polygons built with lineTo, close() is good.
        // The user's original hexagon logic had path.close() inside the when, and another one after.
        // It's generally safe to call close() on a Path if it's intended to be a closed figure for filling/stroking.
        // Since path.close() was inside hexagon and after the when, let's keep one after the when for general polygons.
        // However, for addRect, addCircle, addRoundRect, it's not strictly needed.
        // For safety with custom polygons, let's ensure it's closed if it's a polygon type.
        if (shapeConfig.type.lowercase() == "hexagon" /* || other polygon types */) {
            if (!path.isEmpty) path.close() // Close only if it's a polygon and not empty
        }

        // Apply rotation if specified
        if (shapeConfig.rotationDegrees != 0f && !path.isEmpty) {
            val matrix = android.graphics.Matrix()
            matrix.setRotate(shapeConfig.rotationDegrees, bounds.centerX(), bounds.centerY())
            path.transform(matrix)
        }

        return path
    }
}
