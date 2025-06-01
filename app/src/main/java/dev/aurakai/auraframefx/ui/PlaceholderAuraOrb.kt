package dev.aurakai.auraframefx.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.aurakai.auraframefx.ui.models.AuraMood
import dev.aurakai.auraframefx.ui.models.moodFragmentColors
import dev.aurakai.auraframefx.ui.models.moodGlowConfigs
import kotlinx.coroutines.launch

// --- Orb Animation Constants ---
private const val DEFAULT_ORB_FRAGMENT_COUNT = 8
private const val ROTATION_DURATION_MS = 2000
private const val GLOW_DURATION_MS = 500
private const val FRAGMENT_MOVE_DURATION_MS = 500
private const val FRAGMENT_ROTATION_DURATION_MS = 1000

/**
 * Computes the target X and Y offsets for a fragment based on mood and index.
 */
@Stable
private fun fragmentTargetOffsets(
    mood: AuraMood,
    index: Int,
    fragmentCount: Int,
): Pair<Float, Float> {
    val offsetMultiplier = when (mood) {
        AuraMood.Calm -> 0
        AuraMood.Excited -> 4
        AuraMood.Alert -> 2
        AuraMood.Happy -> 6
        AuraMood.Sad -> 0
        else -> 0
    }
    val offset = (index * offsetMultiplier - (offsetMultiplier * (fragmentCount - 1) / 2)).toFloat()
    return Pair(offset, offset)
}

/**
 * Displays an animated orb whose appearance and animation respond to the current [mood].
 * @param mood The current mood to animate for.
 * @param modifier Modifier for layout and styling.
 */
@Composable
fun PlaceholderAuraOrb(mood: AuraMood, modifier: Modifier = Modifier) {
    Box(modifier.size(100.dp), contentAlignment = Alignment.Center) {
        // Orb configuration
        val fragmentCount = DEFAULT_ORB_FRAGMENT_COUNT
        val rotation = remember { Animatable(0f) }
        val density = LocalDensity.current
        val initialGlowRadius =
            with(density) { moodGlowConfigs[AuraMood.Calm]!!.radius.toPx() } // Use Calm as default initial
        val scope = rememberCoroutineScope()
        // Animatable state
        val glowRadius = remember { Animatable(initialGlowRadius) }
        val glowColor =
            remember { mutableStateOf(moodGlowConfigs[AuraMood.Calm]!!.color.copy(alpha = moodGlowConfigs[AuraMood.Calm]!!.alpha)) } // Initialize with Calm mood
        val fragmentColors = remember {
            mutableStateListOf<Color>().apply {
                repeat(fragmentCount) { add(moodFragmentColors[AuraMood.Calm]!!) } // Initialize with Calm mood
            }
        }

        // Animation definitions
        val rotationAnimation = infiniteRepeatable<Float>(
            animation = tween<Float>(durationMillis = ROTATION_DURATION_MS, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
        val glowAnimation =
            tween<Float>(durationMillis = GLOW_DURATION_MS, easing = FastOutSlowInEasing)

        // Launch animations based on mood
        // Animate orb properties in response to mood changes
        LaunchedEffect(mood) {
            scope.launch {
                val targetGlowConfig = moodGlowConfigs[mood]!!
                rotation.animateTo(
                    when (mood) {
                        AuraMood.Calm -> 360f
                        AuraMood.Excited -> -360f
                        AuraMood.Alert -> 180f
                        AuraMood.Happy -> -180f
                        AuraMood.Sad -> 0f
                        else -> 0f
                    },
                    animationSpec = rotationAnimation
                )
                glowColor.value = targetGlowConfig.color.copy(alpha = targetGlowConfig.alpha)
                glowRadius.animateTo(
                    with(density) { targetGlowConfig.radius.toPx() },
                    animationSpec = glowAnimation
                )
            }
            for (i in 0 until fragmentCount) {
                fragmentColors[i] = moodFragmentColors[mood]!!
            }
        }

        // Glow effect (Canvas)
        Canvas(Modifier.matchParentSize()) {
            drawCircle(
                color = glowColor.value,
                radius = glowRadius.value,
                center = center,
                style = Stroke(width = 8.dp.value)
            )
        }

        // Fragmented orb (using simple circles for now)
        repeat(fragmentCount) { index ->
            val (targetOffsetX, targetOffsetY) = fragmentTargetOffsets(mood, index, fragmentCount)
            val animatedOffsetX by animateFloatAsState(
                targetValue = targetOffsetX,
                animationSpec = tween(
                    durationMillis = FRAGMENT_MOVE_DURATION_MS,
                    easing = FastOutSlowInEasing
                )
            )
            val animatedOffsetY by animateFloatAsState(
                targetValue = targetOffsetY,
                animationSpec = tween(
                    durationMillis = FRAGMENT_MOVE_DURATION_MS,
                    easing = FastOutSlowInEasing
                )
            )
            val animatedRotation by animateFloatAsState(
                targetValue = when (mood) {
                    AuraMood.Calm -> rotation.value
                    AuraMood.Excited -> rotation.value + index * 45f
                    AuraMood.Alert -> rotation.value + index * 20f
                    AuraMood.Happy -> rotation.value + index * 60f
                    AuraMood.Sad -> rotation.value + index * 10f
                    else -> rotation.value
                },
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = FRAGMENT_ROTATION_DURATION_MS,
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "fragmentRotation"
            )
            Fragment(
                color = fragmentColors[index],
                modifier = Modifier
                    .offset(x = animatedOffsetX.dp, y = animatedOffsetY.dp)
                    .rotate(animatedRotation)
            )
        }
    }
}

/**
 * Draws a single orb fragment as a colored circle.
 * @param color The color of the fragment.
 * @param modifier Modifier for layout and styling.
 */
@Composable
fun Fragment(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier.size(16.dp)) {
        drawCircle(color = color, radius = size.minDimension / 2f)
    }
}

/**
 * Preview of the .PlaceholderAuraOrb in all moods.
 */
@Preview
@Composable
fun PlaceholderAuraOrbPreview() {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        PlaceholderAuraOrb(mood = AuraMood.Calm)
        PlaceholderAuraOrb(mood = AuraMood.Excited)
        PlaceholderAuraOrb(mood = AuraMood.Alert)
        PlaceholderAuraOrb(mood = AuraMood.Happy)
        PlaceholderAuraOrb(mood = AuraMood.Sad)
    }
}

/**
 * Preview of .Fragment
 */
@Preview
@Composable
fun FragmentPreview() {
    Fragment(color = Color.Blue)
}
