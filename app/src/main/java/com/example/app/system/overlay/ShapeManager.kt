package com.example.app.system.overlay

import com.example.app.ui.theme.Color
import com.highcapable.yukihookapi.hook.xposed.prefs.data.YukiHookModulePrefs
import com.highcapable.yukihookapi.hook.xposed.service.YukiHookServiceManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShapeManager @Inject constructor(
    private val prefs: YukiHookModulePrefs,
    private val overlayService: YukiHookServiceManager,
) {
    private val activeShapes = mutableMapOf<String, OverlayShape>()
    private val shapePresets = mutableMapOf<String, OverlayShape>()

    init {
        // Load default shape presets
        loadDefaultPresets()
    }

    private fun loadDefaultPresets() {
        // Add default presets
        shapePresets["neon_capsule"] = createNeonCapsule()
        shapePresets["rounded_hexagon"] = createRoundedHexagon()
        shapePresets["glowing_button"] = createGlowingButton()
        shapePresets["wave_border"] = createWaveBorder()
        shapePresets["diamond_badge"] = createDiamondBadge()
    }

    fun createNeonCapsule(): OverlayShape {
        return OverlayShape(
            id = "neon_capsule",
            type = ShapeType.ROUNDED_CAPSULE,
            properties = mapOf(
                "width" to 200,
                "height" to 50,
                "color" to Color(0xFF00FFCC)
            ),
            margins = ShapeMargins(
                left = 16,
                top = 16,
                right = 16,
                bottom = 16
            ),
            padding = ShapePadding(
                left = 12,
                top = 8,
                right = 12,
                bottom = 8
            ),
            border = ShapeBorder(
                width = 2,
                color = "#00FFCC",
                radius = 20f
            ),
            shadow = ShapeShadow(
                color = "#00FFCC",
                offset = ShadowOffset(x = 0f, y = 4f),
                blurRadius = 12f,
                spreadRadius = 2f
            )
        )
    }

    fun createRoundedHexagon(): OverlayShape {
        return OverlayShape(
            id = "rounded_hexagon",
            type = ShapeType.ROUNDED_HEXAGON,
            properties = mapOf(
                "size" to 60,
                "color" to Color(0xFFE000FF)
            ),
            margins = ShapeMargins(
                left = 8,
                top = 8,
                right = 8,
                bottom = 8
            ),
            border = ShapeBorder(
                width = 1,
                color = "#E000FF",
                radius = 8f
            ),
            shadow = ShapeShadow(
                color = "#E000FF",
                offset = ShadowOffset(x = 2f, y = 2f),
                blurRadius = 8f,
                spreadRadius = 1f
            )
        )
    }

    fun createGlowingButton(): OverlayShape {
        return OverlayShape(
            id = "glowing_button",
            type = ShapeType.ROUNDED_BUTTON,
            properties = mapOf(
                "width" to 150,
                "height" to 40,
                "color" to Color(0xFF00FFFF)
            ),
            margins = ShapeMargins(
                left = 12,
                top = 12,
                right = 12,
                bottom = 12
            ),
            padding = ShapePadding(
                left = 16,
                top = 8,
                right = 16,
                bottom = 8
            ),
            border = ShapeBorder(
                width = 2,
                color = "#00FFFF",
                radius = 16f
            ),
            shadow = ShapeShadow(
                color = "#00FFFF",
                offset = ShadowOffset(x = 0f, y = 4f),
                blurRadius = 16f,
                spreadRadius = 4f
            )
        )
    }

    fun createWaveBorder(): OverlayShape {
        return OverlayShape(
            id = "wave_border",
            type = ShapeType.WAVE,
            properties = mapOf(
                "amplitude" to 10,
                "frequency" to 2,
                "color" to Color(0xFF00FFCC)
            ),
            margins = ShapeMargins(
                left = 10,
                top = 10,
                right = 10,
                bottom = 10
            ),
            border = ShapeBorder(
                width = 2,
                color = "#00FFCC"
            ),
            shadow = ShapeShadow(
                color = "#00FFCC",
                offset = ShadowOffset(x = 0f, y = 2f),
                blurRadius = 8f,
                spreadRadius = 1f
            )
        )
    }

    fun createDiamondBadge(): OverlayShape {
        return OverlayShape(
            id = "diamond_badge",
            type = ShapeType.ROUNDED_DIAMOND,
            properties = mapOf(
                "size" to 40,
                "color" to Color(0xFFFF00FF)
            ),
            margins = ShapeMargins(
                left = 6,
                top = 6,
                right = 6,
                bottom = 6
            ),
            border = ShapeBorder(
                width = 1,
                color = "#FF00FF",
                radius = 4f
            ),
            shadow = ShapeShadow(
                color = "#FF00FF",
                offset = ShadowOffset(x = 2f, y = 2f),
                blurRadius = 6f,
                spreadRadius = 1f
            )
        )
    }

    fun applyShape(shape: OverlayShape) {
        activeShapes[shape.id] = shape
        overlayService.hook {
            // TODO: Implement shape hooking logic
        }
    }

    fun removeShape(shapeId: String) {
        activeShapes.remove(shapeId)
        overlayService.hook {
            // TODO: Implement shape removal
        }
    }

    fun getShapePreset(name: String): OverlayShape? {
        return shapePresets[name]
    }

    fun getAllPresets(): Map<String, OverlayShape> {
        return shapePresets.toMap()
    }

    fun createCustomShape(
        type: ShapeType,
        properties: Map<String, Any>,
        margins: ShapeMargins = ShapeMargins(),
        padding: ShapePadding = ShapePadding(),
        border: ShapeBorder = ShapeBorder(),
        shadow: ShapeShadow = ShapeShadow(),
    ): OverlayShape {
        return OverlayShape(
            id = "custom_${System.currentTimeMillis()}",
            type = type,
            properties = properties,
            margins = margins,
            padding = padding,
            border = border,
            shadow = shadow
        )
    }
}
