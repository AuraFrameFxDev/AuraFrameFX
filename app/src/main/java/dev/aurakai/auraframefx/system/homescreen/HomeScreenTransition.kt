package dev.aurakai.auraframefx.system.homescreen // Ensure this package is correct

import kotlinx.serialization.Serializable

@Serializable
public data class HomeScreenTransitionConfig(
    public val defaultOutgoingEffect: HomeScreenTransitionEffect? = null,
    public val defaultIncomingEffect: HomeScreenTransitionEffect? = null,
)

@Serializable
public data class HomeScreenTransitionEffect(
    public val type: String, // e.g., "slide", "fade", "zoom"
    public val properties: TransitionProperties? = null,
)

@Serializable
public data class TransitionProperties(
    public val duration: Long = 300L,
    public val direction: String? = null, // e.g., "left_to_right", "top_to_bottom" for slide
    public val interpolator: String = "linear",
    // Add other relevant properties like scaleFactor for zoom, etc.
)

// Enums like HomeScreenTransitionType do not need @Serializable as per user instructions.
// If this file previously contained other classes or interfaces, they should be preserved.
