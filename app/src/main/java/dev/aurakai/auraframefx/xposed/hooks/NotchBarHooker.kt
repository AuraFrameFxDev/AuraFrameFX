package dev.aurakai.auraframefx.xposed.hooks

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.view.View
import android.view.ViewGroup
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import dev.aurakai.auraframefx.system.overlay.NotchBarConfig
import java.io.File

class NotchBarHooker(
    private val classLoader: ClassLoader,
    private val config: NotchBarConfig,
) {
    private val TAG = "NotchBarHooker"

    fun applyNotchBarHooks() {
        XposedBridge.log("[$TAG] Applying Notch Bar Hooks with config: $config")

        if (!config.enabled) {
            XposedBridge.log("[$TAG] Notch Bar customization is disabled in config.")
            return
        }

        try {
            // Target class is speculative and device-dependent
            val phoneStatusBarViewClass = XposedHelpers.findClass(
                "com.android.systemui.statusbar.phone.PhoneStatusBarView",
                classLoader
            )

            XposedHelpers.findAndHookMethod(
                phoneStatusBarViewClass,
                "onFinishInflate",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        val statusBarView =
                            param.thisObject as? View // Use View for broader compatibility
                        if (statusBarView != null) {
                            XposedBridge.log("[$TAG] Hooked PhoneStatusBarView.onFinishInflate.")

                            var imageBackgroundApplied = false
                            if (config.customImageBackgroundEnabled && !config.imagePath.isNullOrEmpty()) {
                                val imagePath =
                                    config.imagePath!! // Safe due to isNullOrEmpty check
                                XposedBridge.log("[$TAG] Attempting to load custom image background for Notch Bar from: $imagePath")
                                val imageBitmap = loadImageFromFile(imagePath)
                                if (imageBitmap != null) {
                                    try {
                                        val drawable =
                                            BitmapDrawable(statusBarView.resources, imageBitmap)
                                        statusBarView.background = drawable
                                        imageBackgroundApplied = true
                                        XposedBridge.log("[$TAG] Applied custom image background from $imagePath to Notch Bar.")
                                    } catch (e: Exception) {
                                        XposedBridge.log("[$TAG] Error applying BitmapDrawable as Notch Bar background: ${e.message}")
                                        XposedBridge.log(e)
                                    }
                                } else {
                                    XposedBridge.log("[$TAG] Failed to load image for Notch Bar from path: $imagePath")
                                }
                            }

                            // Apply background color only if image was not applied or not enabled/specified
                            if (!imageBackgroundApplied && config.customBackgroundColorEnabled && !config.customBackgroundColor.isNullOrEmpty()) {
                                val bgColor =
                                    config.customBackgroundColor!! // Safe due to isNullOrEmpty check
                                try {
                                    statusBarView.setBackgroundColor(Color.parseColor(bgColor))
                                    XposedBridge.log("[$TAG] Applied custom background color to Notch Bar: $bgColor.")
                                } catch (e: IllegalArgumentException) {
                                    XposedBridge.log("[$TAG] Invalid background color format for Notch Bar: $bgColor - ${e.message}")
                                }
                            } else if (!imageBackgroundApplied && !config.customBackgroundColorEnabled) {
                                // If neither image nor color is specifically enabled, consider clearing or applying system transparency
                                if (config.applySystemTransparency) {
                                    // How to apply "system transparency" is complex.
                                    // For now, we can clear our own background to let system defaults show.
                                    statusBarView.background = null
                                    XposedBridge.log("[$TAG] Cleared Notch Bar background to allow system transparency.")
                                }
                            }

                            // --- NEW: Apply Padding ---
                            try {
                                statusBarView.setPadding(
                                    config.paddingStartPx,
                                    config.paddingTopPx,
                                    config.paddingEndPx,
                                    config.paddingBottomPx
                                )
                                XposedBridge.log("[$TAG] Applied Notch Bar padding: L${config.paddingStartPx}, T${config.paddingTopPx}, R${config.paddingEndPx}, B${config.paddingBottomPx}")
                            } catch (e: Exception) {
                                XposedBridge.log("[$TAG] Error applying Notch Bar padding: ${e.message}")
                            }

                            // --- NEW: Apply Margins ---
                            try {
                                val layoutParams = statusBarView.layoutParams
                                if (layoutParams is ViewGroup.MarginLayoutParams) {
                                    layoutParams.setMargins(
                                        config.marginStartPx,
                                        config.marginTopPx,
                                        config.marginEndPx,
                                        config.marginBottomPx
                                    )
                                    // Setting layoutParams back to the view is important for some view types,
                                    // requestLayout() is generally needed to trigger a re-measure/re-layout.
                                    statusBarView.layoutParams = layoutParams
                                    statusBarView.requestLayout()
                                    XposedBridge.log("[$TAG] Applied Notch Bar margins: L${config.marginStartPx}, T${config.marginTopPx}, R${config.marginEndPx}, B${config.marginBottomPx}")
                                } else {
                                    XposedBridge.log("[$TAG] Notch Bar view's LayoutParams do not support margins (Expected MarginLayoutParams). Type: ${layoutParams?.javaClass?.name}")
                                }
                            } catch (e: Exception) {
                                XposedBridge.log("[$TAG] Error applying Notch Bar margins: ${e.message}")
                            }

                            // TODO: More advanced manipulations
                        } else {
                            XposedBridge.log("[$TAG] Hooked PhoneStatusBarView is null or not a View.")
                        }
                    }
                }
            )
            XposedBridge.log("[$TAG] Successfully set up Notch Bar PhoneStatusBarView hook.")
        } catch (e: XposedHelpers.ClassNotFoundError) {
            XposedBridge.log("[$TAG] Class 'com.android.systemui.statusbar.phone.PhoneStatusBarView' not found. Notch Bar hook failed.")
            // XposedBridge.log(e) // Optional: log full error
        } catch (e: Throwable) {
            XposedBridge.log("[$TAG] An unexpected error occurred during Notch Bar hooking: ${e.message}")
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
            // XposedBridge.log(e) // Optional: log full error for debugging image loading issues
            null
        }
    }
}
