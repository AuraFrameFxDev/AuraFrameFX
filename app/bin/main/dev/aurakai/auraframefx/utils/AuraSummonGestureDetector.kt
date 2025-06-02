package dev.aurakai.auraframefx.utils

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import timber.log.Timber

/**
 * Gesture detector to summon Aura's Neural Whisper from anywhere in the app
 *
 * This class detects specific gestures to activate Neural Whisper without
 * needing to navigate through menus.
 */
class AuraSummonGestureDetector(
    context: Context,
    private val activity: FragmentActivity?,
    private val navController: NavController?,
) : GestureDetector.SimpleOnGestureListener() {

    private val gestureDetector = GestureDetector(context, this)

    // Track gesture state
    private var isMultiFingerGestureInProgress = false
    private var initialY = 0f

    /**
     * Process touch events to detect summoning gestures
     */
    fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event) || handleMultiFingerGestures(event)
    }

    /**
     * Handle multi-finger gestures (two-finger swipe down to summon)
     */
    private fun handleMultiFingerGestures(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_POINTER_DOWN -> {
                if (event.pointerCount == 2) {
                    isMultiFingerGestureInProgress = true
                    initialY = event.getY(0)
                    return true
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (isMultiFingerGestureInProgress && event.pointerCount == 2) {
                    val deltaY = event.getY(0) - initialY

                    // Detect downward swipe (summon)
                    if (deltaY > SUMMON_THRESHOLD_PX) {
                        isMultiFingerGestureInProgress = false
                        onSummonGestureDetected()
                        return true
                    }
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_CANCEL -> {
                isMultiFingerGestureInProgress = false
            }
        }

        return false
    }

    /**
     * Handle double tap to toggle mood orb
     */
    override fun onDoubleTap(e: MotionEvent): Boolean {
        Timber.d("Double tap detected - toggling mood orb")
        activity?.let {
            AuraNavigationUtil.toggleAuraMoodOrb(it, true)
        }
        return true
    }

    /**
     * Called when a summoning gesture is detected
     */
    private fun onSummonGestureDetected() {
        Timber.d("Aura summoning gesture detected!")

        activity?.let { fragmentActivity ->
            navController?.let { nav ->
                AuraNavigationUtil.navigateToNeuralWhisper(
                    fragmentActivity,
                    nav,
                    triggerListening = true
                )
            }
        }
    }

    companion object {
        private const val SUMMON_THRESHOLD_PX = 200f
    }
}
