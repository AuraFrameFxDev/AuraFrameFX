package dev.aurakai.auraframefx.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

/**
 * A composable that provides a holographic transition effect for its content
 * @param visible Whether the content should be visible
 * @param content The content to display with the holographic transition effect
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HologramTransition(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    val enterTransition = remember {
        fadeIn(
            animationSpec = tween(durationMillis = 500)
        ) + expandVertically(
            animationSpec = tween(durationMillis = 500)
        )
    }
    
    val exitTransition = remember {
        fadeOut(
            animationSpec = tween(durationMillis = 500)
        ) + shrinkVertically(
            animationSpec = tween(durationMillis = 500)
        )
    }

    AnimatedVisibility(
        visible = visible,
        enter = enterTransition,
        exit = exitTransition,
        modifier = modifier,
        content = content
    )
}
