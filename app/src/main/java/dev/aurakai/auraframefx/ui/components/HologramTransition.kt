package dev.aurakai.auraframefx.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer // Added for Modifier.graphicsLayer
import kotlin.math.* // Added import
import kotlin.random.Random // Added import

@Composable
public fun HologramTransition(
    visible: Boolean,
    modifier: Modifier = Modifier,
    glowColor: Color = Color(0xFF00FFCC), // Neon teal
    glitchColor: Color = Color(0xFFB388FF), // Neon purple
    backgroundColor: Color = Color.Black,
    content: @Composable () -> Unit,
) {
    // Animate progress: 0f (start/dispersed) to 1f (fully materialized)
    public val progress by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing)
    )

    Box(modifier = modifier.fillMaxSize()) {
        // Hologram effect layer
        Canvas(modifier = Modifier.fillMaxSize()) {
            public val width = size.width
            public val height = size.height
            public val blockSize = 24f
            public val scanlineSpacing = 8f
            public val dispersion = (1f - progress) * height

            // Draw pitch black background
            drawRect(backgroundColor)

            // Scanline shimmer
            for (y in 0..(height / scanlineSpacing).roundToInt()) {
                public val alpha = 0.10f + 0.15f * (1f - progress)
                drawRect(
                    color = glowColor.copy(alpha = alpha),
                    topLeft = Offset(0f, y * scanlineSpacing),
                    size = androidx.compose.ui.geometry.Size(width, 2f * progress)
                )
            }

            // Blocky pixel materialization/dispersal
            for (x in 0..(width / blockSize).roundToInt()) {
                for (y in 0..(height / blockSize).roundToInt()) {
                    public val blockY = y * blockSize
                    // Blocks rise/fall into place during transition
                    public val blockProgress = progress + 0.2f * kotlin.math.sin(x + y * 0.5f)
                    public val reveal = (blockY < height * blockProgress)
                    if (reveal) {
                        drawRect(
                            color = glowColor.copy(alpha = 0.13f + 0.10f * kotlin.math.sin(x * 0.7f + y)),
                            topLeft = Offset(x * blockSize, blockY),
                            size = androidx.compose.ui.geometry.Size(blockSize, blockSize)
                        )
                    } else {
                        // Glitchy dispersal
                        if (progress < 1f && kotlin.random.Random.nextFloat() > progress) {
                            drawRect(
                                color = glitchColor.copy(alpha = 0.11f),
                                topLeft = Offset(
                                    x * blockSize,
                                    blockY + dispersion * kotlin.random.Random.nextFloat()
                                ),
                                size = androidx.compose.ui.geometry.Size(blockSize, blockSize)
                            )
                        }
                    }
                }
            }
        }
        // Content fades in as hologram forms
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = progress } // Corrected usage of graphicsLayer
        ) {
            content() // Content is drawn inside the Box with the modifier
        }
    }
}
