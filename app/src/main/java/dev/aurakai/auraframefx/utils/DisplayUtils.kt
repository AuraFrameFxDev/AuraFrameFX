package dev.aurakai.auraframefx.utils

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager

/**
 * Utility object for display-related functions.
 */
object DisplayUtils {

    /**
     * Gets the display metrics of the current device.
     *
     * @param context The application context.
     * @return DisplayMetrics object containing screen information.
     */
    fun getDisplayMetrics(context: Context): DisplayMetrics {
        // TODO: Consider if this needs to be more sophisticated, e.g., for specific displays
        val displayMetrics = DisplayMetrics()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics
    }

    /**
     * Gets the status bar height.
     *
     * @param context The application context.
     * @return The height of the status bar in pixels, or 0 if it cannot be determined.
     */
    fun getStatusBarHeight(context: Context): Int {
        // TODO: This is a common way but might not be universally accurate.
        // Consider edge cases and newer Android versions (WindowInsets).
        var result = 0
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    /**
     * Converts density-independent pixels (dp) to pixels (px).
     *
     * @param dp The value in dp.
     * @param context The application context.
     * @return The value in pixels.
     */
    fun dpToPx(dp: Float, context: Context): Float {
        return dp * context.resources.displayMetrics.density
    }

    /**
     * Converts pixels (px) to density-independent pixels (dp).
     *
     * @param px The value in pixels.
     * @param context The application context.
     * @return The value in dp.
     */
    fun pxToDp(px: Float, context: Context): Float {
        return px / context.resources.displayMetrics.density
    }
}
