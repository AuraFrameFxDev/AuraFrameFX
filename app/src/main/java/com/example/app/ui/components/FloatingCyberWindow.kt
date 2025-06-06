package com.example.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.app.ui.animation.DigitalTransitions
import com.example.app.ui.theme.*
import kotlin.math.*

/**
 * A floating transparent window with a cyberpunk design
 * Based on the reference designs that show transparent panels with neon borders and hexagonal elements
 */
@Composable
fun FloatingCyberWindow(
    modifier: Modifier = Modifier,
    title: String? = null,
    cornerStyle: CornerStyle = CornerStyle.Hex,
    borderGlow: Boolean = true,
    backgroundStyle: BackgroundStyle = BackgroundStyle.Transparent,
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cyberWindow")
    
    val borderGlowIntensity by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowPulse"
    )
    
    val clipShape = when (cornerStyle) {
        CornerStyle.Hex -> CyberpunkShapes.hexWindowShape
        CornerStyle.Angled -> CyberpunkShapes.angledWindowShape
        CornerStyle.Rounded -> RoundedCornerShape(16.dp)
    }

    // Main container
    Box(
        modifier = modifier
            .clip(clipShape)
            // Apply transparent background with slight blur to create depth
            .then(
                when (backgroundStyle) {
                    BackgroundStyle.Transparent -> Modifier.background(
                        Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                            )
                        )
                    )
                    BackgroundStyle.HexGrid -> Modifier.background(Color.Black.copy(alpha = 0.7f))
                    BackgroundStyle.DigitalLandscape -> Modifier.background(Color.Black.copy(alpha = 0.85f))
                }
            )
            // Apply border glow
            .then(
                if (borderGlow) {
                    Modifier.drawWithCache {
                        val width = size.width
                        val height = size.height
                        
                        onDrawBehind {
                            // Draw inner border
                            drawRect(
                                color = NeonBlue.copy(alpha = 0.8f * borderGlowIntensity),
                                style = Stroke(width = 1.5f)
                            )
                            
                            // Draw outer glow
                            drawRect(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        NeonBlue.copy(alpha = 0.5f * borderGlowIntensity),
                                        NeonBlue.copy(alpha = 0f)
                                    ),
                                    center = Offset(width / 2, height / 2),
                                    radius = max(width, height) / 1.5f
                                ),
                                blendMode = BlendMode.Screen
                            )
                            
                            // Draw corner accents
                            val cornerSize = 12f
                            val corners = listOf(
                                Offset(0f, 0f),
                                Offset(width, 0f),
                                Offset(0f, height),
                                Offset(width, height)
                            )
                            
                            corners.forEachIndexed { index, offset ->
                                // Alternate colors for corner accents
                                val cornerColor = if (index % 2 == 0) NeonCyan else NeonPink
                                drawCircle(
                                    color = cornerColor.copy(alpha = borderGlowIntensity),
                                    radius = cornerSize / 2,
                                    center = offset,
                                    blendMode = BlendMode.Screen
                                )
                            }
                        }
                    }
                } else Modifier
            )
    ) {
        // Add background style if required
        when (backgroundStyle) {
            BackgroundStyle.HexGrid -> {
                HexagonGridBackground(
                    modifier = Modifier.matchParentSize(),
                    alpha = 0.15f
                )
            }
            BackgroundStyle.DigitalLandscape -> {
                DigitalLandscapeBackground(
                    modifier = Modifier.matchParentSize()
                )
            }
            else -> { /* No additional background */ }
        }
        
        // Window title if present
        title?.let { windowTitle ->
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-1).dp)
                    .zIndex(10f)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                NeonBlue.copy(alpha = 0.3f),
                                NeonPink.copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        ),
                        shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent, 
                                NeonBlue, 
                                NeonPink,
                                Color.Transparent
                            )
                        ),
                        shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)
                    )
                    .padding(horizontal = 20.dp, vertical = 6.dp)
            ) {
                CyberpunkText(
                    text = windowTitle,
                    color = CyberpunkTextColor.Primary,
                    style = CyberpunkTextStyle.Header
                )
            }
        }
        
        // Window content
        Box(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            content = content
        )
        
        // Add scanline effect overlay
        Box(
            modifier = Modifier
                .matchParentSize()
                .DigitalTransitions.digitalScanlineEffect()
        )
    }
}

/**
 * Special text component for cyberpunk UI with digital glitch effects
 */
@Composable
fun CyberpunkText(
    text: String,
    modifier: Modifier = Modifier,
    color: CyberpunkTextColor = CyberpunkTextColor.Primary,
    style: CyberpunkTextStyle = CyberpunkTextStyle.Body,
    enableGlitch: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "textGlitch")
    val glitchChance by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000),
            repeatMode = RepeatMode.Restart
        ),
        label = "glitchChance"
    )
    
    val textColor = when (color) {
        CyberpunkTextColor.Primary -> NeonCyan
        CyberpunkTextColor.Secondary -> NeonPink
        CyberpunkTextColor.Accent -> NeonBlue
        CyberpunkTextColor.Warning -> NeonYellow
        CyberpunkTextColor.White -> Color.White
    }
    
    val textStyle = when (style) {
        CyberpunkTextStyle.Header -> MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )
        CyberpunkTextStyle.Title -> MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        CyberpunkTextStyle.Body -> MaterialTheme.typography.bodyMedium.copy(
            letterSpacing = 0.5.sp
        )
        CyberpunkTextStyle.Label -> MaterialTheme.typography.labelMedium.copy(
            letterSpacing = 1.sp
        )
        CyberpunkTextStyle.Glitch -> MaterialTheme.typography.bodyLarge.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 3.sp
        )
    }
    
    // Apply digital glitch effect occasionally
    val displayText = if (enableGlitch && glitchChance > 0.95f) {
        val random = kotlin.random.Random(glitchChance.hashCode())
        val glitchPos = random.nextInt(text.length)
        if (glitchPos < text.length - 1) {
            text.substring(0, glitchPos) + 
                    (text[glitchPos].code + 10).toChar() + 
                    text.substring(glitchPos + 1)
        } else {
            text
        }
    } else {
        text
    }
    
    Box(modifier = modifier) {
        // Shadow/Glow effect
        Text(
            text = displayText,
            style = textStyle,
            color = textColor.copy(alpha = 0.3f),
            modifier = Modifier
                .offset(x = 1.dp, y = 1.dp)
                .blur(2.dp, BlurredEdgeTreatment.Unbounded)
        )
        
        // Main text
        Text(
            text = displayText,
            style = textStyle,
            color = textColor
        )
    }
}

enum class CornerStyle {
    Hex,
    Angled,
    Rounded
}

enum class BackgroundStyle {
    Transparent,
    HexGrid,
    DigitalLandscape
}

enum class CyberpunkTextColor {
    Primary,
    Secondary,
    Accent,
    Warning,
    White
}

enum class CyberpunkTextStyle {
    Header,
    Title,
    Body,
    Label,
    Glitch
}

/**
 * A floating menu item in cyberpunk style based on the reference images
 */
@Composable
fun CyberMenuItem(
    text: String,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(CyberpunkShapes.angledButtonShape)
            .background(
                brush = if (isSelected) {
                    Brush.horizontalGradient(
                        colors = listOf(
                            NeonBlue.copy(alpha = 0.2f),
                            NeonPink.copy(alpha = 0.3f)
                        )
                    )
                } else {
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.5f),
                            Color.Black.copy(alpha = 0.7f)
                        )
                    )
                }
            )
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    colors = if (isSelected) {
                        listOf(NeonBlue, NeonPink)
                    } else {
                        listOf(Color.White.copy(alpha = 0.3f), Color.White.copy(alpha = 0.5f))
                    }
                ),
                shape = CyberpunkShapes.angledButtonShape
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            CyberpunkText(
                text = text.uppercase(),
                color = if (isSelected) CyberpunkTextColor.Primary else CyberpunkTextColor.White,
                style = CyberpunkTextStyle.Label
            )
            
            if (isSelected) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .background(NeonCyan, RoundedCornerShape(50))
                )
            }
        }
    }
}
