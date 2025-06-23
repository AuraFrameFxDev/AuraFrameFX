package dev.aurakai.auraframefx.ui.animation.transitions

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 * Digital transition effects for a cyberpunk aesthetic.
 * Provides pixelation and scanline effects that can be applied to composables.
 */
object DigitalTransitions {
    
    /**
     * Returns a Modifier that conditionally applies pixelation and scanline digital effects.
     *
     * Combines pixelation and scanline visual effects as modifiers based on the provided flags, enabling a cyberpunk digital aesthetic for composables.
     *
     * @param modifier The base Modifier to which effects will be applied.
     * @param pixelationEnabled If true, applies a pixelation overlay effect.
     * @param scanlineEnabled If true, applies a moving scanline overlay effect.
     * @return A Modifier with the selected digital effects applied.
     */
    fun applyDigitalEffects(
        modifier: Modifier = Modifier,
        pixelationEnabled: Boolean = true,
        scanlineEnabled: Boolean = true
    ): Modifier = modifier.then(
        if (pixelationEnabled) digitalPixelEffect() else Modifier
    ).then(
        if (scanlineEnabled) digitalScanlineEffect() else Modifier
    )

    /**
     * Applies a pixelation overlay effect to the composable, simulating a digitized appearance.
     *
     * The effect overlays a grid of semi-transparent rectangles to create a pixelated look. The visibility of the effect is animated with a fade-in/out transition.
     *
     * @param visible Whether the pixelation effect is visible.
     * @return The modifier with the pixelation effect applied if visible; otherwise, the original modifier.
     */
    fun Modifier.digitalPixelEffect(visible: Boolean = true): Modifier = composed {
        val pixelSize = 4.dp
        val transition = updateTransition(visible, label = "pixel_effect")
        val alpha by transition.animateFloat(
            transitionSpec = { tween(300) },
            label = "pixel_alpha"
        ) { if (it) 1f else 0f }

        if (alpha > 0f) {
            this.then(
                Modifier.drawWithContent {
                    val pixelWidth = pixelSize.toPx()
                    val pixelHeight = pixelSize.toPx()
                    
                    // Draw the content
                    drawContent()
                    
                    // Apply pixelation overlay
                    drawIntoCanvas { canvas ->
                        val paint = Paint().apply {
                            blendMode = BlendMode.SrcAtop
                            alpha = 0.2f * alpha
                        }
                        
                        val width = size.width
                        val height = size.height
                        
                        for (x in 0 until width.toInt() step pixelWidth.toInt()) {
                            for (y in 0 until height.toInt() step pixelHeight.toInt()) {
                                canvas.drawRect(
                                    left = x.toFloat(),
                                    top = y.toFloat(),
                                    right = (x + pixelWidth).coerceAtMost(width),
                                    bottom = (y + pixelHeight).coerceAtMost(height),
                                    paint = paint
                                )
                            }
                        }
                    }
                }
            )
        } else {
            this
        }
    }

    /**
     * Applies an animated digital scanline effect overlay to the composable.
     *
     * When enabled, overlays moving horizontal cyan scanlines across the composable area, simulating a digital display. The scanlines animate vertically in a continuous loop and fade in or out based on the `visible` parameter.
     *
     * @param visible Whether the scanline effect is visible and animated.
     * @return The modifier with the scanline effect applied if visible; otherwise, the original modifier.
     */
    fun Modifier.digitalScanlineEffect(visible: Boolean = true): Modifier = composed {
        val scanlineHeight = 1.dp
        val scanlineSpacing = 4.dp
        val animationDuration = 2000
        
        val transition = updateTransition(visible, label = "scanline_effect")
        val alpha by transition.animateFloat(
            transitionSpec = { tween(300) },
            label = "scanline_alpha"
        ) { if (it) 0.05f else 0f }
        
        val infiniteTransition = rememberInfiniteTransition()
        val scanlinePosition by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(animationDuration, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "scanline_position"
        )

        if (alpha > 0f) {
            this.then(
                Modifier.drawWithContent {
                    drawContent()
                    
                    val height = size.height
                    val lineHeight = scanlineHeight.toPx()
                    val spacing = scanlineSpacing.toPx()
                    
                    drawIntoCanvas { canvas ->
                        val paint = Paint().apply {
                            color = Color.Cyan.copy(alpha = alpha)
                            blendMode = BlendMode.Screen
                        }
                        
                        // Draw multiple scanlines
                        var y = -lineHeight + (height + lineHeight) * scanlinePosition
                        while (y < height) {
                            canvas.drawRect(
                                left = 0f,
                                top = y,
                                right = size.width,
                                bottom = y + lineHeight,
                                paint = paint
                            )
                            y += lineHeight + spacing
                        }
                    }
                }
            )
        } else {
            this
        }
    }
}
