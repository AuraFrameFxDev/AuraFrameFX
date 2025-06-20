package dev.aurakai.auraframefx.ui.theme

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Dimensions for the AuraFrameFX app
 * Using Kotlin object instead of dimens.xml for better integration with Compose
 */
public object AppDimensions {
    // Spacing
    public val spacing_xs = 4.dp
    public val spacing_small = 8.dp
    public val spacing_medium = 16.dp
    public val spacing_large = 24.dp
    public val spacing_xl = 32.dp
    public val spacing_xxl = 48.dp

    // Component Sizing
    public val button_height = 48.dp
    public val button_min_width = 120.dp
    public val icon_size_small = 16.dp
    public val icon_size_medium = 24.dp
    public val icon_size_large = 32.dp

    // Text Sizes
    public val text_size_xs = 12.sp
    public val text_size_small = 14.sp
    public val text_size_medium = 16.sp
    public val text_size_large = 18.sp
    public val text_size_xl = 20.sp
    public val text_size_xxl = 24.sp

    // Corner Radii
    public val corner_radius_small = 4.dp
    public val corner_radius_medium = 8.dp
    public val corner_radius_large = 12.dp
    public val corner_radius_xl = 16.dp
    public val corner_radius_round = 50.dp

    // Elevation
    public val elevation_small = 2.dp
    public val elevation_medium = 4.dp
    public val elevation_large = 8.dp

    // Stroke Width
    public val stroke_small = 1.dp
    public val stroke_medium = 2.dp
    public val stroke_large = 3.dp

    // Card Sizes
    public val card_min_height = 64.dp
    public val card_padding = 16.dp
}
