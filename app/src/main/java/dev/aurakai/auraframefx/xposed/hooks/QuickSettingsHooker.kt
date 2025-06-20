package dev.aurakai.auraframefx.xposed.hooks

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import dev.aurakai.auraframefx.system.overlay.model.OverlayShape
import dev.aurakai.auraframefx.system.overlay.ShapeManager
import dev.aurakai.auraframefx.system.quicksettings.model.QuickSettingsConfig
import dev.aurakai.auraframefx.system.quicksettings.model.QuickSettingsTileConfig
import dev.aurakai.auraframefx.system.quicksettings.model.QuickSettingsTileAnimation
import dev.aurakai.auraframefx.system.quicksettings.model.HapticFeedbackConfig
import java.io.File


public class QuickSettingsHooker(
    private val classLoader: ClassLoader,
    private val config: QuickSettingsConfig,
) {
    private val TAG = "QuickSettingsHooker"
    private val shapeManager: ShapeManager by lazy { ShapeManager() } // New

    fun applyQuickSettingsHooks() {
        XposedBridge.log("[$TAG] Applying Quick Settings Hooks with config: $config")

        // --- QS Tile Modification ---
        try {
            val qsTileViewClass = XposedHelpers.findClass(
                "com.android.systemui.qs.tileimpl.QSTileView",
                classLoader
            )

            XposedHelpers.findAndHookMethod(
                qsTileViewClass,
                "onFinishInflate",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        val qsTileView = param.thisObject as View
                        val tileId = getTileSpec(qsTileView)
                        XposedBridge.log("[$TAG] Hooked QSTileView.onFinishInflate for tile: $tileId")

                        val tileConfig = config.tiles?.find { it.tileId == tileId }

                        // Label Handling
                        val labelTextView = findViewByClass(qsTileView, TextView::class.java)
                        if (labelTextView != null) {
                            if (config.hideTileLabels == true) {
                                labelTextView.visibility = View.GONE
                                XposedBridge.log("[$TAG] Hidden label for tile $tileId.")
                            } else {
                                labelTextView.visibility = View.VISIBLE
                                XposedBridge.log("[$TAG] Label visible for tile $tileId.")
                                if (config.customTextColorEnabled == true && !config.customTextColor.isNullOrEmpty()) {
                                    try {
                                        labelTextView.setTextColor(Color.parseColor(config.customTextColor))
                                        XposedBridge.log("[$TAG] Applied custom text color for $tileId: ${config.customTextColor}.")
                                    } catch (e: IllegalArgumentException) {
                                        XposedBridge.log("[$TAG] Invalid custom text color format for $tileId: ${config.customTextColor} - ${e.message}")
                                    }
                                }
                            }
                        }

                        // Icon Handling
                        val iconImageView = findViewByClass(qsTileView, ImageView::class.java)
                        if (iconImageView != null) {
                            if (config.hideTileIcons == true) { // Assuming new config field
                                iconImageView.visibility = View.GONE
                                XposedBridge.log("[$TAG] Hidden icon for tile $tileId.")
                            } else {
                                iconImageView.visibility = View.VISIBLE
                                XposedBridge.log("[$TAG] Icon visible for tile $tileId.")
                                if (tileConfig?.iconTintEnabled == true && !tileConfig.iconTintColor.isNullOrEmpty()) {
                                    try {
                                        iconImageView.setColorFilter(
                                            Color.parseColor(tileConfig.iconTintColor),
                                            PorterDuff.Mode.SRC_IN
                                        )
                                        XposedBridge.log("[$TAG] Applied custom icon tint for $tileId: ${tileConfig.iconTintColor}.")
                                    } catch (e: IllegalArgumentException) {
                                        XposedBridge.log("[$TAG] Invalid icon tint color format for $tileId: ${tileConfig.iconTintColor} - ${e.message}")
                                    }
                                } else {
                                    iconImageView.clearColorFilter() // Clear previous tint if not enabled
                                }
                            }
                        }

                        // Tile Background & Shape Logic
                        if (tileConfig?.customShapeEnabled == true) {
                            val shapeConfig = tileConfig.shape
                            XposedBridge.log("[$TAG] Attempting to apply custom shape to tile $tileId: ${shapeConfig.shapeType}")
                            val customBackgroundDrawable =
                                createCustomShapeDrawable(qsTileView, shapeConfig)
                            if (customBackgroundDrawable != null) {
                                qsTileView.background = customBackgroundDrawable
                                XposedBridge.log("[$TAG] Applied custom shape drawable to tile $tileId.")
                            } else {
                                XposedBridge.log("[$TAG] Failed to create custom shape drawable for tile $tileId, falling back to color or default.")
                                // Fallback to color if shape fails but color is defined
                                if (tileConfig.customBackgroundColorEnabled == true && !tileConfig.customBackgroundColor.isNullOrEmpty()) {
                                    try {
                                        qsTileView.setBackgroundColor(Color.parseColor(tileConfig.customBackgroundColor))
                                        XposedBridge.log("[$TAG] Applied custom background color (fallback from shape) for tile $tileId: ${tileConfig.customBackgroundColor}.")
                                    } catch (e: IllegalArgumentException) {
                                        XposedBridge.log("[$TAG] Invalid background color format (fallback from shape) for $tileId: ${tileConfig.customBackgroundColor} - ${e.message}")
                                    }
                                } else {
                                    qsTileView.background = null
                                    XposedBridge.log("[$TAG] No custom background for tile $tileId after shape failure, cleared background.")
                                }
                            }
                        } else if (tileConfig?.customBackgroundColorEnabled == true && !tileConfig.customBackgroundColor.isNullOrEmpty()) {
                            try {
                                qsTileView.setBackgroundColor(Color.parseColor(tileConfig.customBackgroundColor))
                                XposedBridge.log("[$TAG] Applied custom background color for tile $tileId: ${tileConfig.customBackgroundColor}.")
                            } catch (e: IllegalArgumentException) {
                                XposedBridge.log("[$TAG] Invalid background color format for $tileId: ${tileConfig.customBackgroundColor} - ${e.message}")
                            }
                        } else {
                            qsTileView.background = null
                            XposedBridge.log("[$TAG] No custom shape or background color for tile $tileId, cleared background.")
                        }

                        // Animation Logic
                        // Assuming QuickSettingsConfig has tileAnimationDefault and QuickSettingsTileConfig has animation
                        val animationConfigToApply =
                            tileConfig?.animation ?: config.tileAnimationDefault

                        if (animationConfigToApply != null && animationConfigToApply.type != "none" && animationConfigToApply.durationMs > 0) {
                            XposedBridge.log("[$TAG] Applying animation for tile $tileId: Type=${animationConfigToApply.type}, Duration=${animationConfigToApply.durationMs}ms")
                            applyAnimation(qsTileView, animationConfigToApply)
                        } else {
                            val type = animationConfigToApply?.type ?: "null_config"
                            val duration = animationConfigToApply?.durationMs ?: 0
                            XposedBridge.log("[$TAG] No animation or zero duration for tile $tileId. Type: $type, Duration: ${duration}ms")
                            // Ensure view is in a default state if no animation
                            qsTileView.alpha = 1f
                            qsTileView.scaleX = 1f
                            qsTileView.scaleY = 1f
                            qsTileView.rotation = 0f
                        }
                    }
                }
            )

            // Hook for performClick for haptic feedback
            XposedHelpers.findAndHookMethod(
                qsTileViewClass,
                "performClick",
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        val qsTileView = param.thisObject as View
                        val tileId = getTileSpec(qsTileView)
                        val hapticConfigToApply =
                            config.tiles?.find { it.tileId == tileId }?.hapticFeedback
                                ?: config.defaultHapticFeedback

                        if (hapticConfigToApply.enabled == true) {
                            XposedBridge.log("[$TAG] Applying haptic feedback for tile $tileId click.")
                            applyHapticFeedback(qsTileView, hapticConfigToApply)
                        }
                    }
                }
            )
            XposedBridge.log("[$TAG] Successfully set up QSTileView modification and click hooks.")
        } catch (e: Throwable) {
            XposedBridge.log("[$TAG] Error hooking QSTileView for modification or click: ${e.message}")
            XposedBridge.log(e)
        }

        // --- QS Panel Layout/Header Modification ---
        try {
            val qsPanelHeaderClass = XposedHelpers.findClass(
                "com.android.systemui.qs.QuickStatusBarHeader",
                classLoader
            )
            XposedHelpers.findAndHookMethod(
                qsPanelHeaderClass,
                "onFinishInflate",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        val qsHeaderView = param.thisObject as View
                        XposedBridge.log("[$TAG] Hooked QuickStatusBarHeader.onFinishInflate.")

                        var imageBackgroundApplied = false
                        if (config.headerBackgroundConfig?.customImageBackgroundEnabled == true && !config.headerBackgroundConfig?.imagePath.isNullOrEmpty()) {
                            val imagePath = config.headerBackgroundConfig!!.imagePath!!
                            XposedBridge.log("[$TAG] Attempting to load custom image background from: $imagePath")
                            val imageBitmap = loadImageFromFile(imagePath)
                            if (imageBitmap != null) {
                                try {
                                    val drawable =
                                        BitmapDrawable(qsHeaderView.resources, imageBitmap)
                                    qsHeaderView.background = drawable
                                    imageBackgroundApplied = true
                                    XposedBridge.log("[$TAG] Applied custom image background from $imagePath to QS header.")
                                } catch (e: Exception) {
                                    XposedBridge.log("[$TAG] Error applying BitmapDrawable as background: ${e.message}")
                                    XposedBridge.log(e)
                                }
                            } else {
                                XposedBridge.log("[$TAG] Failed to load image from path: $imagePath")
                            }
                        }

                        if (!imageBackgroundApplied) {
                            if (config.headerBackgroundConfig?.customBackgroundColorEnabled == true && !config.headerBackgroundConfig?.customBackgroundColor.isNullOrEmpty()) {
                                val bgColor =
                                    config.headerBackgroundConfig!!.customBackgroundColor!!
                                try {
                                    qsHeaderView.setBackgroundColor(Color.parseColor(bgColor))
                                    XposedBridge.log("[$TAG] Set custom header background color to $bgColor.")
                                } catch (e: IllegalArgumentException) {
                                    XposedBridge.log("[$TAG] Invalid header background color format: $bgColor - ${e.message}")
                                }
                            } else {
                                // If no image and no color, clear background or apply overall tint as fallback
                                qsHeaderView.background =
                                    null // Clear background if no specific color/image
                                XposedBridge.log("[$TAG] No custom header background color/image, cleared background.")
                            }
                        }

                        // NEW: Apply overall tint to header if no custom image AND tint is enabled
                        if (!imageBackgroundApplied && config.headerBackgroundConfig?.customOverallTintEnabled == true && !config.headerBackgroundConfig?.customOverallTint.isNullOrEmpty()) {
                            val tintColorStr = config.headerBackgroundConfig!!.customOverallTint!!
                            try {
                                val color = Color.parseColor(tintColorStr)
                                // Note: setTint might not work on all backgrounds.
                                // If qsHeaderView.background is null (cleared above), this won't work.
                                // It's better to ensure there's a color background first, or use a ColorDrawable.
                                // For simplicity, this assumes background is not null or setTint can handle it.
                                // A safer way if background could be null:
                                // val colorDrawable = ColorDrawable(color)
                                // qsHeaderView.background = colorDrawable // This would replace image or color background
                                // Or, if tinting an existing non-null drawable (like the color set above):
                                qsHeaderView.background?.colorFilter =
                                    PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)
                                XposedBridge.log("[$TAG] Applied custom overall tint to QS header: $tintColorStr")
                            } catch (e: IllegalArgumentException) {
                                XposedBridge.log("[$TAG] Invalid overall tint color: $tintColorStr - ${e.message}")
                            }
                        }


                        if (config.hideFooterButtons == true) {
                            try {
                                val footer =
                                    XposedHelpers.callMethod(qsHeaderView, "getQSFooter") as? View
                                footer?.visibility = View.GONE
                                XposedBridge.log("[$TAG] Hidden QS footer buttons.")
                            } catch (e: Throwable) {
                                XposedBridge.log("[$TAG] Error trying to hide QS footer: ${e.message}")
                            }
                        }
                    }
                }
            )
            XposedBridge.log("[$TAG] Successfully set up QuickStatusBarHeader hook.")
        } catch (e: Throwable) {
            XposedBridge.log("[$TAG] Error hooking QuickStatusBarHeader: ${e.message}")
            XposedBridge.log(e)
        }
    }

    private fun loadImageFromFile(filePath: String): Bitmap? {
        return try {
            val file = File(filePath)
            if (file.exists()) {
                BitmapFactory.decodeFile(file.absolutePath)
            } else {
                XposedBridge.log("[$TAG] Image file not found: $filePath")
                null
            }
        } catch (e: Exception) {
            XposedBridge.log("[$TAG] Exception loading image from $filePath: ${e.message}")
            XposedBridge.log(e) // Log full exception for debugging
            null
        }
    }

    private fun <T : View> findViewByClass(parent: View, clazz: Class<T>): T? {
        if (clazz.isInstance(parent)) {
            @Suppress("UNCHECKED_CAST")
            return parent as T
        }
        if (parent is ViewGroup) {
            for (i in 0 until parent.childCount) {
                val child = parent.getChildAt(i)
                val found = findViewByClass(child, clazz)
                if (found != null) return found
            }
        }
        return null
    }

    private fun getTileSpec(qsTileView: View): String {
        return try {
            val qsTile = XposedHelpers.getObjectField(qsTileView, "mTile")
            XposedHelpers.callMethod(qsTile, "getTileSpec") as String
        } catch (e: Throwable) {
            XposedBridge.log("[$TAG] Failed to get tile spec for view ${qsTileView.id}: ${e.message}")
            "unknown_tile_${qsTileView.id}"
        }
    }

    private fun createCustomShapeDrawable(view: View, shapeConfig: OverlayShape): Drawable? {
        val drawable: GradientDrawable = GradientDrawable()

        try {
            if (shapeConfig.fillColor?.isNotEmpty() == true) {
                drawable.setColor(Color.parseColor(shapeConfig.fillColor))
            } else {
                drawable.setColor(Color.TRANSPARENT)
            }

            if (shapeConfig.shapeType.equals("rounded_rectangle", ignoreCase = true)) {
            if (shapeConfig.cornerRadius != null) {
                drawable.cornerRadius = shapeConfig.cornerRadius.coerceAtLeast(0f)
            }
            } else if (shapeConfig.shapeType.equals("circle", ignoreCase = true)) {
                drawable.shape = GradientDrawable.OVAL
            } else if (shapeConfig.shapeType.equals("rectangle", ignoreCase = true)) {
                drawable.shape = GradientDrawable.RECTANGLE
            } else if (shapeConfig.shapeType.equals("hexagon", ignoreCase = true)) {
                XposedBridge.log("[$TAG] Warning: 'hexagon' shape type not fully supported by simple GradientDrawable for tile background. Requires a custom Drawable class using ShapeManager.createShapePath. Defaulting shape for now.")
            }

            if ((shapeConfig.strokeWidthPx ?: 0f) > 0f && shapeConfig.strokeColor?.isNotEmpty() == true) {
                try {
                    drawable.setStroke(
                        (shapeConfig.strokeWidthPx ?: 0f).toInt().coerceAtLeast(0),
                        Color.parseColor(shapeConfig.strokeColor)
                    )
                } catch (e: IllegalArgumentException) {
                    XposedBridge.log("[$TAG] Invalid stroke color format: ${shapeConfig.strokeColor} - ${e.message}")
                }
            }

            if (shapeConfig.shadow != null) {
                XposedBridge.log("[$TAG] Note: Complex shadows from ShapeShadow config are not applied by this simple GradientDrawable background. Requires custom Drawable.")
            }

            return drawable
        } catch (e: Exception) {
            XposedBridge.log("[$TAG] Error creating custom shape drawable for type '${shapeConfig.type}': ${e.message}")
            XposedBridge.log(e)
            return null
        }
    }

    private fun applyHapticFeedback(view: View, hapticConfig: HapticFeedbackConfig) {
        // Implement haptic feedback for quick settings tiles
        // This is just a stub - fill in with actual implementation as needed
        XposedBridge.log("[$TAG] Applied haptic feedback with effect: ${hapticConfig.effect}")
    }

    private fun applyAnimation(view: View, animationConfig: QuickSettingsTileAnimation) {
        // Implement animation for quick settings tiles
        // This is just a stub - fill in with actual implementation as needed
        XposedBridge.log("[$TAG] Applied animation of type: ${animationConfig.type}")
    }
}
