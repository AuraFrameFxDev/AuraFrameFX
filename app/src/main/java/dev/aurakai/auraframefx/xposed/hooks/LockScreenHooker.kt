package dev.aurakai.auraframefx.xposed.hooks

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.TextClock
import android.widget.TextView
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import dev.aurakai.auraframefx.system.lockscreen.HapticFeedbackConfig
import dev.aurakai.auraframefx.system.lockscreen.LockScreenAnimation
import dev.aurakai.auraframefx.system.lockscreen.LockScreenConfig

class LockScreenHooker(
    private val classLoader: ClassLoader,
    private val config: LockScreenConfig,
) {
    private val TAG = "LockScreenHooker"

    fun applyLockScreenHooks() {
        XposedBridge.log("[$TAG] Applying Lock Screen Hooks with config: $config")

        // --- Lock Screen Clock Modification ---
        try {
            val textClockClass = XposedHelpers.findClass(
                "android.widget.TextClock",
                classLoader
            )

            XposedHelpers.findAndHookMethod(
                textClockClass,
                "onAttachedToWindow",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        val textClockView = param.thisObject as? TextClock
                        if (textClockView != null) {
                            XposedBridge.log("[$TAG] Hooked TextClock.onAttachedToWindow for Lock Screen: $textClockView")

                            val clockConfig = config.clockConfig
                            if (clockConfig != null) {
                                // Apply custom text color
                                if (clockConfig.customTextColorEnabled == true && !clockConfig.customTextColor.isNullOrEmpty()) {
                                    try {
                                        textClockView.setTextColor(Color.parseColor(clockConfig.customTextColor))
                                        XposedBridge.log("[$TAG] Set custom clock text color to ${clockConfig.customTextColor}.")
                                    } catch (e: IllegalArgumentException) {
                                        XposedBridge.log("[$TAG] Invalid clock text color: ${clockConfig.customTextColor} - ${e.message}")
                                    }
                                }

                                // Apply custom text size
                                if (clockConfig.customTextSizeEnabled == true && clockConfig.customTextSizeSp > 0) {
                                    try {
                                        textClockView.setTextSize(
                                            TypedValue.COMPLEX_UNIT_SP,
                                            clockConfig.customTextSizeSp.toFloat()
                                        )
                                        XposedBridge.log("[$TAG] Set custom clock text size to ${clockConfig.customTextSizeSp}sp.")
                                    } catch (e: Exception) {
                                        XposedBridge.log("[$TAG] Error setting custom clock text size: ${e.message}")
                                    }
                                }

                                // Apply custom font style (NEW)
                                if (!clockConfig.customFontStyle.isNullOrEmpty()) {
                                    try {
                                        val style =
                                            when (clockConfig.customFontStyle?.lowercase()) {
                                                "bold" -> Typeface.BOLD
                                                "italic" -> Typeface.ITALIC
                                                "bold_italic" -> Typeface.BOLD_ITALIC
                                                else -> {
                                                    XposedBridge.log("[$TAG] Unsupported font style: ${clockConfig.customFontStyle}. Using default.")
                                                    Typeface.NORMAL // Default or textClockView.typeface.style
                                                }
                                            }
                                        textClockView.setTypeface(
                                            textClockView.typeface,
                                            style
                                        ) // Preserve existing family, change style
                                        XposedBridge.log("[$TAG] Applied custom clock font style: ${clockConfig.customFontStyle}.")
                                    } catch (e: Exception) {
                                        XposedBridge.log("[$TAG] Error applying clock font style: ${e.message}")
                                    }
                                }

                                // Apply Animation to Clock
                                val clockAnimationConfig =
                                    clockConfig.animation // clockConfig is already checked for null
                                if (clockAnimationConfig.type != "none" && clockAnimationConfig.durationMs > 0) {
                                    XposedBridge.log("[$TAG] Applying animation to Lock Screen Clock: Type=${clockAnimationConfig.type}")
                                    applyAnimation(textClockView, clockAnimationConfig)
                                } else {
                                    // Reset animation properties if none specified
                                    textClockView.alpha = 1f
                                    textClockView.translationY = 0f
                                    textClockView.scaleX = 1f
                                    textClockView.scaleY = 1f
                                    XposedBridge.log("[$TAG] No animation for Lock Screen Clock or zero duration.")
                                }
                            } else {
                                XposedBridge.log("[$TAG] clockConfig is null within LockScreenConfig.")
                            }
                        } else {
                            XposedBridge.log("[$TAG] Hooked view on Lock Screen is not a TextClock instance or is null.")
                        }
                    }
                }
            )
            XposedBridge.log("[$TAG] Successfully set up Lock Screen TextClock hook.")
        } catch (e: Throwable) {
            XposedBridge.log("[$TAG] Error hooking Lock Screen clock (TextClock): ${e.message}")
            XposedBridge.log(e)
        }

        // --- Control Lock Screen Date Visibility (NEW HOOK) ---
        if (config.hideDate == true) { // Check this general config first
            try {
                // This class name is an example and highly device/version dependent.
                // Common containers for date/time on lock screen.
                val keyguardStatusViewClass = XposedHelpers.findClass(
                    "com.android.keyguard.KeyguardStatusView",
                    classLoader
                )

                XposedHelpers.findAndHookMethod(
                    keyguardStatusViewClass,
                    "onFinishInflate", // Or another suitable lifecycle method
                    object : XC_MethodHook() {
                        override fun afterHookedMethod(param: MethodHookParam) {
                            val statusView = param.thisObject as? View
                            if (statusView != null) {
                                XposedBridge.log("[$TAG] Hooked KeyguardStatusView.onFinishInflate.")
                                // Attempt to find the date TextView. This is speculative.
                                // It might have a specific ID, or be one of several TextViews.
                                // For this example, let's assume it's the first TextView found,
                                // or one with specific characteristics if identifiable.
                                // A more robust solution would involve ID lookup if known, or more specific class.
                                val dateTextView =
                                    findDateView(statusView) // Using a more specific helper
                                if (dateTextView != null) {
                                    dateTextView.visibility = View.GONE
                                    XposedBridge.log("[$TAG] Hidden Lock Screen date view: $dateTextView")
                                    // Apply Animation to Date (if visible - this logic is slightly off as it's just been hidden)
                                    // The prompt implies animation should apply if it *would have been* visible.
                                    // For now, let's assume if hideDate is true, we don't animate.
                                    // If hideDate is false (handled in the 'else' below), then we animate.
                                } else {
                                    XposedBridge.log("[$TAG] Could not find date TextView within KeyguardStatusView.")
                                }
                            }
                        }
                    }
                )
                XposedBridge.log("[$TAG] Successfully set up Lock Screen date visibility hook for KeyguardStatusView.")
            } catch (e: XposedHelpers.ClassNotFoundError) {
                XposedBridge.log("[$TAG] Class 'com.android.keyguard.KeyguardStatusView' not found. Cannot hook date visibility.")
                // XposedBridge.log(e) // Optional: log full error
            } catch (e: Throwable) {
                XposedBridge.log("[$TAG] Error hooking Lock Screen date visibility: ${e.message}")
                XposedBridge.log(e)
            }
        } else {
            XposedBridge.log("[$TAG] Lock Screen date visibility not modified (hideDate is false or null).")
            // If hideDate is false, we might want to apply animations if the date view is found
            // This requires finding the date view again or having a reference.
            // For simplicity, let's assume the hook on KeyguardStatusView also handles this if not hidden.
            // The provided snippet structure implies animation logic is within the KeyguardStatusView hook.
            // Let's refine the KeyguardStatusView hook to handle visibility AND animation.
            try {
                val keyguardStatusViewClass =
                    XposedHelpers.findClass("com.android.keyguard.KeyguardStatusView", classLoader)
                XposedHelpers.findAndHookMethod(
                    keyguardStatusViewClass,
                    "onFinishInflate",
                    object : XC_MethodHook() {
                        override fun afterHookedMethod(param: MethodHookParam) {
                            val statusView = param.thisObject as? View ?: return
                            val dateTextView = findDateView(statusView)
                            if (dateTextView != null) {
                                if (config.hideDate == true) {
                                    dateTextView.visibility = View.GONE
                                    XposedBridge.log("[$TAG] Hidden Lock Screen date view: $dateTextView")
                                } else {
                                    dateTextView.visibility =
                                        View.VISIBLE // Ensure visible if not hidden
                                    XposedBridge.log("[$TAG] Lock Screen date view visible: $dateTextView")
                                    // Apply Animation to Date (if visible)
                                    val dateAnimationConfig = config.dateConfig?.animation
                                        ?: config.defaultElementAnimation
                                    if (dateAnimationConfig.type != "none" && dateAnimationConfig.durationMs > 0) {
                                        XposedBridge.log("[$TAG] Applying animation to Lock Screen Date: Type=${dateAnimationConfig.type}")
                                        applyAnimation(dateTextView, dateAnimationConfig)
                                    } else {
                                        dateTextView.alpha = 1f
                                        dateTextView.translationY = 0f
                                        dateTextView.scaleX = 1f
                                        dateTextView.scaleY = 1f
                                        XposedBridge.log("[$TAG] No animation for Lock Screen Date or zero duration.")
                                    }
                                }
                            } else {
                                XposedBridge.log("[$TAG] Could not find date TextView within KeyguardStatusView for visibility/animation.")
                            }
                        }
                    })
            } catch (e: Throwable) {
                // Catching generic throwable if findClass or findAndHookMethod fails when hideDate is false.
                XposedBridge.log("[$TAG] Error setting up date view hook for animation when date is not hidden: ${e.message}")
            }
        }

        // --- Haptic Feedback on KeyguardSimView (Example Hook) ---
        // This is a generic example; specific interactions (e.g., unlock, PIN entry) would need more targeted hooks.
        try {
            val keyguardSimViewClass = XposedHelpers.findClass(
                "com.android.keyguard.KeyguardSimView", // Example target class
                classLoader
            )
            XposedHelpers.findAndHookMethod(
                keyguardSimViewClass,
                "onFinishInflate", // Hooking a lifecycle method for simplicity
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        val view = param.thisObject as? View
                        if (view != null) {
                            XposedBridge.log("[$TAG] Hooked KeyguardSimView.onFinishInflate for haptics.")
                            val hapticConfig = config.hapticFeedback // Get global LS haptic config
                            if (hapticConfig.enabled == true) {
                                // This will trigger haptic on view inflation, which might be too early/frequent.
                                // A more specific interaction hook (e.g. on a button click within this view)
                                // would be better for real-world use.
                                XposedBridge.log("[$TAG] Applying haptic feedback to KeyguardSimView inflation (example).")
                                applyHapticFeedback(view.context, hapticConfig)
                            }
                        }
                    }
                }
            )
            XposedBridge.log("[$TAG] Successfully set up KeyguardSimView haptic feedback hook (example).")
        } catch (e: XposedHelpers.ClassNotFoundError) {
            XposedBridge.log("[$TAG] Class 'com.android.keyguard.KeyguardSimView' not found. Cannot hook for haptics.")
        } catch (e: Throwable) {
            XposedBridge.log("[$TAG] Error hooking KeyguardSimView for haptics: ${e.message}")
            XposedBridge.log(e)
        }
    }

    private fun applyHapticFeedback(context: Context, hapticConfig: HapticFeedbackConfig) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator

        if (vibrator?.hasVibrator() == true) {
            val effectId: Int? = when (hapticConfig.effect.lowercase()) {
                "click" -> VibrationEffect.EFFECT_CLICK
                "double_click" -> VibrationEffect.EFFECT_DOUBLE_CLICK
                "heavy_click" -> VibrationEffect.EFFECT_HEAVY_CLICK
                "tick" -> VibrationEffect.EFFECT_TICK
                else -> {
                    XposedBridge.log("[$TAG] Unsupported haptic effect type: ${hapticConfig.effect}")
                    null
                }
            }

            if (effectId != null) {
                val vibrationEffect: VibrationEffect
                // minSdkVersion is 31, so API >= 26 (O) is always true
                if (true) {
                    if (hapticConfig.effect.lowercase() == "click" && hapticConfig.intensity != 50) {
                        val scaledAmplitude =
                            (hapticConfig.intensity / 100f * 255f).toInt().coerceIn(1, 255)
                        vibrationEffect = VibrationEffect.createOneShot(10, scaledAmplitude)
                        XposedBridge.log("[$TAG] Applying custom 'click' with intensity (amplitude ${scaledAmplitude}).")
                    } else {
                        vibrationEffect = VibrationEffect.createPredefined(effectId)
                        if (hapticConfig.intensity != 50 && hapticConfig.effect.lowercase() != "click") {
                            XposedBridge.log("[$TAG] Note: Custom intensity (${hapticConfig.intensity}) for predefined effect '${hapticConfig.effect}' may not be fully supported on all devices/APIs. Using predefined effect.")
                        }
                    }
                } else { // API < 26
                    @Suppress("DEPRECATION")
                    when (hapticConfig.effect.lowercase()) {
                        "click" -> vibrator.vibrate(10)
                        else -> {
                            XposedBridge.log("[$TAG] Haptic effect '${hapticConfig.effect}' not simple to replicate on API < 26. Skipping.")
                            return // Return early as no vibrationEffect is created
                        }
                    }
                    XposedBridge.log("[$TAG] Applied haptic feedback (legacy API): Effect: ${hapticConfig.effect}")
                    return // Return as vibrate already called for legacy
                }
                vibrator.vibrate(vibrationEffect)
                XposedBridge.log("[$TAG] Applied haptic feedback: Effect ID ${effectId}, Config Effect: ${hapticConfig.effect}, Intensity: ${hapticConfig.intensity}")
            } else {
                XposedBridge.log("[$TAG] Haptic effect ID is null or effect type '${hapticConfig.effect}' is 'none' or unsupported.")
            }
        } else {
            XposedBridge.log("[$TAG] Device does not have a vibrator or vibrator service is null.")
        }
    }

    private fun applyAnimation(view: View, animationConfig: LockScreenAnimation) {
        val animatorSet = AnimatorSet()
        val animators = mutableListOf<Animator>()

        when (animationConfig.type.lowercase()) { // Use lowercase
            "fade_in" -> {
                view.alpha = 0f
                animators.add(ObjectAnimator.ofFloat(view, "alpha", 0f, 1f))
            }

            "slide_up" -> {
                view.alpha = 0f
                view.translationY = view.height.toFloat() * 0.5f
                animators.add(ObjectAnimator.ofFloat(view, "alpha", 0f, 1f))
                animators.add(ObjectAnimator.ofFloat(view, "translationY", view.translationY, 0f))
            }

            "scale_in" -> {
                view.alpha = 0f
                view.scaleX = 0.5f
                view.scaleY = 0.5f
                animators.add(ObjectAnimator.ofFloat(view, "alpha", 0f, 1f))
                animators.add(ObjectAnimator.ofFloat(view, "scaleX", 0.5f, 1f))
                animators.add(ObjectAnimator.ofFloat(view, "scaleY", 0.5f, 1f))
            }

            else -> {
                XposedBridge.log("[$TAG] Unsupported animation type for Lock Screen: ${animationConfig.type}")
                return
            }
        }

        if (animators.isEmpty()) {
            XposedBridge.log("[$TAG] No animators created for type: ${animationConfig.type}")
            return
        }

        animatorSet.playTogether(animators)
        animatorSet.duration = animationConfig.durationMs
        animatorSet.startDelay = animationConfig.startDelayMs

        when (animationConfig.interpolator.lowercase()) { // Use lowercase
            "linear" -> animatorSet.interpolator = LinearInterpolator()
            "accelerate" -> animatorSet.interpolator = AccelerateInterpolator()
            "decelerate" -> animatorSet.interpolator = DecelerateInterpolator()
            "accelerate_decelerate" -> animatorSet.interpolator = AccelerateDecelerateInterpolator()
            else -> {
                XposedBridge.log("[$TAG] Unsupported interpolator: ${animationConfig.interpolator}. Using default (AccelerateDecelerate).")
                animatorSet.interpolator = AccelerateDecelerateInterpolator() // Default
            }
        }

        animatorSet.start()
        XposedBridge.log("[$TAG] Started '${animationConfig.type}' animation on Lock Screen view. Duration: ${animationConfig.durationMs}ms, Delay: ${animationConfig.startDelayMs}ms, Interpolator: ${animationConfig.interpolator}")
    }

    // Helper to find a child view by its class type (can be shared or in a common util)
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

    // More specific helper for finding a date view (example, needs refinement)
    private fun findDateView(parent: View): TextView? {
        // This is highly heuristic. A real implementation would need
        // to find the date view by ID (if available through resources)
        // or by a more unique characteristic.
        if (parent is ViewGroup) {
            for (i in 0 until parent.childCount) {
                val child = parent.getChildAt(i)
                if (child is TextView) {
                    // Example heuristic: date usually contains day, month, or year.
                    // This is unreliable.
                    val text = child.text.toString().lowercase()
                    if (text.contains("mon") || text.contains("tue") || text.contains("wed") ||
                        text.contains("thu") || text.contains("fri") || text.contains("sat") || text.contains(
                            "sun"
                        ) ||
                        text.contains("jan") || text.contains("feb") || text.contains("mar") || /* etc. */
                        Regex("\\d{1,2}/\\d{1,2}").containsMatchIn(text) || Regex("\\d{4}").containsMatchIn(
                            text
                        )
                    ) {
                        // This is a very rough guess.
                        // XposedBridge.log("[$TAG] Potential date TextView found: ${child.text}")
                        // return child // For now, let's assume the first textview is not it. Needs specific ID.
                    }
                    // For beta, if no specific ID, this might be too error-prone.
                    // The user's snippet didn't specify how to find the date TextView,
                    // so this part is highly speculative.
                    // A common approach is to hook a method that directly gives you the date view.
                }
                // Recursively search in child ViewGroups if this is a ViewGroup
                if (child is ViewGroup) {
                    val foundInChild = findDateView(child)
                    if (foundInChild != null) return foundInChild
                }
            }
        }
        // Fallback: For this beta version, if KeyguardStatusView is hooked,
        // assume the first TextView child *might* be the date, or a prominent one.
        // This is NOT ROBUST.
        if (parent is ViewGroup && parent.childCount > 0 && parent.getChildAt(0) is TextView) {
            // XposedBridge.log("[$TAG] Fallback: Assuming first TextView in KeyguardStatusView might be related to date/status.")
            // return parent.getChildAt(0) as TextView
        }
        // Returning null as a safe default if no reliable way to find it.
        return null
    }
}
