package dev.aurakai.auraframefx.utils

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowInsets
import android.view.WindowManager

/**
 * Utility object for display-related functions.
 */
object DisplayUtils {

    /**
     * Retrieves the display metrics for the current device screen.
     *
     * @return A [DisplayMetrics] object containing information such as screen size and density.
     */
    fun getDisplayMetrics(context: Context): DisplayMetrics {
        // TODO: Consider if this needs to be more sophisticated, e.g., for specific displays
        val displayMetrics = DisplayMetrics()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics
    }

    /**
     * Retrieves the height of the status bar in pixels.
     *
     * For Android API level 30 and above, obtains the status bar height using `WindowInsets`. For lower API levels, retrieves the value from system resources. Returns 0 if the height cannot be determined.
     *
     * @return The status bar height in pixels, or 0 if unavailable.
     */
    fun getStatusBarHeight(context: Context): Int {
        // Prefer WindowInsets for API 30+; fallback for older APIs
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            val windowInsets = context.getSystemService(WindowManager::class.java)
                ?.currentWindowMetrics?.windowInsets
            windowInsets?.getInsets(WindowInsets.Type.statusBars())?.top ?: 0
        } else {
            var result = 0
            val resourceId =
                context.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = context.resources.getDimensionPixelSize(resourceId)
            }
            result
        }
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
