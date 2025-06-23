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
     * Applies digital effects to the composable.
     * @param modifier The modifier to apply the effects to.
     * @param pixelationEnabled Whether to apply pixelation effect.
     * @param scanlineEnabled Whether to apply scanline effect.
     * @return A modified modifier with the digital effects applied.
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
     * Creates a pixelation effect that makes the content appear digitized.
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
     * Creates a scanline effect that moves down the screen.
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
