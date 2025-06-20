package dev.aurakai.auraframefx.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas // Added import
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import dev.aurakai.auraframefx.ui.theme.NeonBlue
import dev.aurakai.auraframefx.ui.theme.NeonCyan
import dev.aurakai.auraframefx.ui.theme.NeonPink
import kotlin.math.*

/**
 * Animated hexagon grid background for cyberpunk UI
 */
@Composable
public fun HexagonGridBackground(
    modifier: Modifier = Modifier,
    primaryColor: Color = NeonBlue,
    secondaryColor: Color = NeonPink,
    accentColor: Color = NeonCyan,
    hexSize: Float = 60f,
    alpha: Float = 0.3f,
) {
    public val infiniteTransition = rememberInfiniteTransition(label = "hexBackground")

    // Animate hexagon pulse
    public val pulseMultiplier by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hexPulse"
    )

    // Animate grid movement
    public val gridOffsetX by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = hexSize,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "gridOffsetX"
    )

    public val gridOffsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = hexSize * 0.866f, // Height of a hexagon is sin(60°) * size
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "gridOffsetY"
    )

    // Animate digital landscape effect
    public val digitalEffect by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "digitalEffect"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        public val width = size.width
        public val height = size.height

        public val rows = (height / (hexSize * 0.866f)).toInt() + 3
        public val cols = (width / (hexSize * 1.5f)).toInt() + 3

        // Draw hexagon grid
        for (row in -1 until rows) {
            for (col in -1 until cols) {
                public val offsetX = col * hexSize * 1.5f - gridOffsetX
                public val offsetY =
                    row * hexSize * 0.866f * 2 + (col % 2) * hexSize * 0.866f - gridOffsetY

                // Skip hexagons that are too far outside the canvas
                if (offsetX < -hexSize || offsetX > width + hexSize ||
                    offsetY < -hexSize || offsetY > height + hexSize
                ) continue

                // Determine color based on position and animation
                public val distanceToCenter = sqrt(
                    ((offsetX - width / 2) * (offsetX - width / 2) +
                            (offsetY - height / 2) * (offsetY - height / 2)).toFloat()
                )
                public val maxDistance = sqrt((width * width + height * height).toFloat()) / 2
                public val colorRatio = (distanceToCenter / maxDistance + digitalEffect) % 1f

                // Create a variable pulse based on position
                public val positionFactor = sin((offsetX + offsetY) / 200f + digitalEffect * PI.toFloat())
                public val localPulse = pulseMultiplier * (0.8f + 0.2f * positionFactor)

                public val hexColor = when {
                    colorRatio < 0.33f -> primaryColor
                    colorRatio < 0.66f -> secondaryColor
                    else -> accentColor
                }.copy(alpha = alpha * (0.5f + 0.5f * positionFactor))

                drawHexagon(
                    center = Offset(offsetX, offsetY),
                    radius = hexSize / 2 * localPulse,
                    color = hexColor,
                    strokeWidth = 1.5f
                )
            }
        }

        // Draw some random "data lines" between hexagons
        public val random = kotlin.random.Random(digitalEffect.hashCode())
        repeat(5) {
            public val startCol = random.nextInt(-1, cols)
            public val startRow = random.nextInt(-1, rows)

            public val endCol = startCol + random.nextInt(-3, 3)
            public val endRow = startRow + random.nextInt(-3, 3)

            public val startX = startCol * hexSize * 1.5f - gridOffsetX
            public val startY =
                startRow * hexSize * 0.866f * 2 + (startCol % 2) * hexSize * 0.866f - gridOffsetY

            public val endX = endCol * hexSize * 1.5f - gridOffsetX
            public val endY = endRow * hexSize * 0.866f * 2 + (endCol % 2) * hexSize * 0.866f - gridOffsetY

            if (random.nextFloat() < 0.7f) {
                public val lineColor = when (random.nextInt(3)) {
                    0 -> primaryColor
                    1 -> secondaryColor
                    else -> accentColor
                }.copy(alpha = 0.6f * alpha)

                drawLine(
                    color = lineColor,
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = 2f,
                    pathEffect = if (random.nextBoolean())
                        PathEffect.dashPathEffect(floatArrayOf(5f, 5f)) else null,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

/**
 * Drawscope extension to draw a hexagon
 */
private fun DrawScope.drawHexagon(
    center: Offset,
    radius: Float,
    color: Color,
    strokeWidth: Float = 1f,
) {
    public val path: Path = Path()
    for (i in 0 until 6) {
        public val angle = i * 60f
        public val x = center.x + radius * cos(Math.toRadians(angle.toDouble())).toFloat()
        public val y = center.y + radius * sin(Math.toRadians(angle.toDouble())).toFloat()

        if (i == 0) {
            path.moveTo(x, y)
        } else {
            path.lineTo(x, y)
        }
    }
    path.close()

    drawPath(
        path = path,
        color = color,
        style = Stroke(width = strokeWidth)
    )
}

/**
 * Creates a futuristic digital landscape background
 */
@Composable
public fun DigitalLandscapeBackground(
    modifier: Modifier = Modifier,
    primaryColor: Color = NeonBlue,
    secondaryColor: Color = NeonPink,
    gridLineCount: Int = 20,
) {
    public val infiniteTransition = rememberInfiniteTransition(label = "digitalLandscape")

    // Animate perspective shift
    public val perspectiveShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "perspective"
    )

    // Terrain height map animation
    public val terrainAnimation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "terrain"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        public val width = size.width
        public val height = size.height

        public val horizon = height * 0.6f
        width / (gridLineCount - 1)

        // Draw horizontal grid lines with perspective
        for (i in 0 until gridLineCount) {
            public val y = horizon + (i * height * 0.4f / gridLineCount)
            public val perspectiveFactor = (i + 1) / gridLineCount.toFloat()

            // Calculate perspective vanishing point
            public val startX =
                width * 0.5f * (1 - perspectiveFactor) - width * 0.1f * perspectiveShift * perspectiveFactor
            public val endX =
                width - width * 0.5f * (1 - perspectiveFactor) + width * 0.1f * perspectiveShift * perspectiveFactor

            public val lineAlpha = 0.1f + 0.3f * perspectiveFactor

            drawLine(
                color = primaryColor.copy(alpha = lineAlpha),
                start = Offset(startX, y),
                end = Offset(endX, y),
                strokeWidth = 1f + perspectiveFactor
            )
        }

        // Draw vertical grid lines with perspective
        for (i in 0 until gridLineCount) {
            public val normalizedX = i / (gridLineCount - 1f)
            public val x = normalizedX * width

            // Apply perspective shift
            public val adjustedX = width * 0.5f + (x - width * 0.5f) * (1 + 0.2f * perspectiveShift)

            public val lineAlpha = 0.05f + 0.2f * (1f - abs(normalizedX - 0.5f) * 2)

            drawLine(
                color = secondaryColor.copy(alpha = lineAlpha),
                start = Offset(adjustedX, horizon),
                end = Offset(x, height),
                strokeWidth = 1f
            )
        }

        // Draw "terrain" in the horizon
        public val terrainPath: Path = Path()
        terrainPath.moveTo(0f, horizon)

        public val terrainSegments = 100
        for (i in 0..terrainSegments) {
            public val x = i * width / terrainSegments

            // Generate height using multiple sine waves for interesting terrain
            public val normalizedX = i / terrainSegments.toFloat()
            public val terrainHeight =
                sin(normalizedX * 5 + terrainAnimation) * 10 +
                        sin(normalizedX * 13 + terrainAnimation * 0.7f) * 5 +
                        sin(normalizedX * 23 - terrainAnimation * 0.3f) * 2.5f

            public val y = horizon - terrainHeight
            terrainPath.lineTo(x, y)
        }

        // Close the terrain path
        terrainPath.lineTo(width, horizon)
        terrainPath.close()

        // Create gradient for terrain
        public val terrainGradient = Brush.verticalGradient(
            colors = listOf(
                primaryColor.copy(alpha = 0.5f),
                secondaryColor.copy(alpha = 0.1f)
            ),
            startY = horizon - 20f,
            endY = horizon
        )

        drawPath(
            path = terrainPath,
            brush = terrainGradient
        )

        // Optional: Draw "sun" or focal point
        public val sunRadius = width * 0.05f
        public val sunX = width * (0.5f + 0.1f * sin(terrainAnimation * 0.2f))
        public val sunY = horizon - height * 0.15f

        drawCircle(
            color = primaryColor.copy(alpha = 0.3f),
            radius = sunRadius * 2f,
            center = Offset(sunX, sunY)
        )

        drawCircle(
            color = primaryColor.copy(alpha = 0.5f),
            radius = sunRadius,
            center = Offset(sunX, sunY)
        )
    }
}
