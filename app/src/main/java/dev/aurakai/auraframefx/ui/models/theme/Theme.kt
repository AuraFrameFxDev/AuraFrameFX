package dev.aurakai.auraframefx.ui.theme

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext

// Dark Color Scheme - Cyberpunk Neon Noir
private val AuraDarkColorScheme = darkColorScheme(
    // Primary Colors
    primary = NeonTeal,
    onPrimary = OnPrimary,
    primaryContainer = NeonBlue,
    onPrimaryContainer = OnPrimary,

    // Secondary Colors
    secondary = NeonPurple,
    onSecondary = OnSurface,
    secondaryContainer = NeonPurple,
    onSecondaryContainer = OnSurface,

    // Tertiary Colors
    tertiary = NeonBlue,
    onTertiary = OnSurface,
    tertiaryContainer = NeonBlue,
    onTertiaryContainer = OnSurface,

    // Background Colors
    background = DarkBackground,
    onBackground = OnSurface,

    // Surface Colors
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,

    // Outline and Borders
    outline = NeonPurple,

    // System Colors
    error = ErrorColor,
    onError = OnPrimary,
    errorContainer = ErrorColor,
    onErrorContainer = OnPrimary,

    // Additional Colors
    inversePrimary = NeonTeal,
    inverseOnPrimary = OnPrimary,
    inverseSurface = Surface
)

// Light Color Scheme - Enhanced for Cyberpunk
private val AuraLightColorScheme = lightColorScheme(
    // Primary Colors
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimary,
    onPrimaryContainer = LightOnPrimary,

    // Secondary Colors
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    secondaryContainer = LightSecondary,
    onSecondaryContainer = LightOnSecondary,

    // Tertiary Colors
    tertiary = LightTertiary,
    onTertiary = LightOnTertiary,
    tertiaryContainer = LightTertiary,
    onTertiaryContainer = LightOnTertiary,

    // Background Colors
    background = LightBackground,
    onBackground = LightOnBackground,

    // Surface Colors
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurface,
    onSurfaceVariant = LightOnBackground,

    // Outline and Borders
    outline = LightSecondary,

    // System Colors
    error = LightError,
    onError = LightOnError,
    errorContainer = LightError,
    onErrorContainer = LightOnError,

    // Additional Colors
    inversePrimary = LightPrimary,
    inverseOnPrimary = LightOnPrimary,
    inverseSurface = LightSurface
)

/**
 * Applies the AuraFrameFX cyberpunk neon noir theme to the provided composable content.
 *
 * Selects a color scheme based on the dark theme setting and dynamic color support, and applies custom typography and shapes.
 * The content is wrapped in a Box with a vertical gradient background for a distinctive visual effect.
 *
 * @param darkTheme Whether to use the dark color scheme. Defaults to the system setting.
 * @param dynamicColor Whether to use dynamic color on supported Android versions.
 * @param content The composable content to which the theme will be applied.
 */
@Composable
fun AuraFrameFXTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        darkTheme -> AuraDarkColorScheme
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            dynamicLightColorScheme(context)
        }

        else -> AuraLightColorScheme
    }

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            DarkBackground,
            Surface,
            SurfaceVariant
        )
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(brush = backgroundBrush)
            ) {
                content()
            }
        }
    )
}
