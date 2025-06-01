package dev.aurakai.auraframefx.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

val Teal = Color(0xFF009688)

@Composable
fun AuraSparkleButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    moodColor: Color = Teal,
) {
    var buttonColor by remember { mutableStateOf(moodColor) }
    val animatedColor by animateColorAsState(
        targetValue = buttonColor,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )
    val sparkleRadius = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(animatedColor, animatedColor.copy(alpha = 0.8f))
                )
            )
            .clickable {
                onClick()
                // Simple hue shift simulation: blend with white for a quick effect
                buttonColor = moodColor.copy(
                    red = (moodColor.red + 0.1f).coerceAtMost(1f),
                    green = (moodColor.green + 0.1f).coerceAtMost(1f),
                    blue = (moodColor.blue + 0.1f).coerceAtMost(1f)
                )
                coroutineScope.launch {
                    sparkleRadius.animateTo(
                        targetValue = 40f,
                        animationSpec = tween(
                            durationMillis = 300,
                            easing = FastOutSlowInEasing
                        )
                    )
                    sparkleRadius.animateTo(
                        targetValue = 0f,
                        animationSpec = tween(
                            durationMillis = 300,
                            easing = FastOutSlowInEasing
                        )
                    )
                }
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            }
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 18.sp
        )
        Canvas(Modifier.matchParentSize()) {
            if (sparkleRadius.value > 0f) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.6f),
                    radius = sparkleRadius.value,
                    center = center
                )
            }
        }
    }
}

@Preview
@Composable
fun AuraSparkleButtonPreview() {
    Row {
        AuraSparkleButton(text = "Happy", onClick = {}, moodColor = Color.Yellow)
        Spacer(modifier = Modifier.width(8.dp))
        AuraSparkleButton(text = "Calm", onClick = {}, moodColor = Color.Cyan)
        Spacer(modifier = Modifier.width(8.dp))
        AuraSparkleButton(text = "Alert", onClick = {}, moodColor = Color.Red)
    }
}
