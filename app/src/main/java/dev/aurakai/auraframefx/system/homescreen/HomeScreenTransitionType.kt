package dev.aurakai.auraframefx.system.homescreen

/**
 * Enum defining different types of home screen transitions.
 */
enum class HomeScreenTransitionType {
    // Basic transitions
    SLIDE_LEFT,
    SLIDE_RIGHT,
    SLIDE_UP,
    SLIDE_DOWN,
    FADE,
    ZOOM,
    ROTATE,

    // Digital transitions
    DIGITAL_DECONSTRUCT,
    DIGITAL_RECONSTRUCT,

    // Card stack transitions
    STACK_SLIDE,
    STACK_FADE,
    STACK_SCALE,
    STACK_ROTATE,

    // Fan transitions
    FAN_IN,
    FAN_OUT,
    FAN_ROTATE,
    FAN_SCALE,

    // Globe transitions
    GLOBE_ROTATE,
    GLOBE_SCALE,
    GLOBE_PULSE,
    GLOBE_GLOW,

    // Spread transitions
    SPREAD_IN,
    SPREAD_OUT,
    SPREAD_ROTATE,
    SPREAD_SCALE,

    // 3D transitions
    STACK_ROTATE_3D,
    STACK_SCALE_3D,
    STACK_SLIDE_3D,
    STACK_WAVE_3D
}
