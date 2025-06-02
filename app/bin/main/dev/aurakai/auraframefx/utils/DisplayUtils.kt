package dev.aurakai.auraframefx.utils

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowInsets
import android.view.WindowManager

/**
 * Utility class for handling display metrics and screen dimensions
 */
object DisplayUtils {

    /**
     * Get the display metrics for the device
     */
    fun getDisplayMetrics(context: Context): DisplayMetrics {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = windowManager.currentWindowMetrics
            val bounds = windowMetrics.bounds
            displayMetrics.widthPixels = bounds.width()
            displayMetrics.heightPixels = bounds.height()
        } else {
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.getMetrics(displayMetrics)
        }

        return displayMetrics
    }

    /**
     * Get the status bar height to help position elements relative to the notch
     */
    fun getStatusBarHeight(context: Context): Int {
        var result = 0

        // Try to get it from Android R+ WindowInsets
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val windowMetrics = windowManager.currentWindowMetrics
            val insets = windowMetrics.windowInsets.getInsetsIgnoringVisibility(
                WindowInsets.Type.statusBars()
            )
            return insets.top
        }

        // Fall back to resource identifier method
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }

        // Use a reasonable default if all else fails
        if (result <= 0) {
            result = dpToPx(24) // Default status bar height
        }

        return result
    }

    /**
     * Get the navigation bar height
     */
    fun getNavigationBarHeight(context: Context): Int {
        var result = 0

        // Try to get it from Android R+ WindowInsets
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val windowMetrics = windowManager.currentWindowMetrics
            val insets = windowMetrics.windowInsets.getInsetsIgnoringVisibility(
                WindowInsets.Type.navigationBars()
            )
            return insets.bottom
        }

        // Fall back to resource identifier method
        val resourceId =
            context.resources.getIdentifier("navigation_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }

        return result
    }

    /**
     * Convert dp to pixels
     */
    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    /**
     * Convert pixels to dp
     */
    fun pxToDp(px: Int): Int {
        return (px / Resources.getSystem().displayMetrics.density).toInt()
    }
}
