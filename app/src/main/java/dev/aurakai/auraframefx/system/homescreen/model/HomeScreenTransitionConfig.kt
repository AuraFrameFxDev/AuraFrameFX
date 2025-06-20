package dev.aurakai.auraframefx.system.homescreen.model

public data class HomeScreenTransitionConfig(
    public val type: HomeScreenTransitionType = HomeScreenTransitionType.GLOBE_ROTATE,
    public val duration: Int = 500,
    public val properties: Map<String, Any> = emptyMap()
)
