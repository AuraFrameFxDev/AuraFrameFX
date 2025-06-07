package dev.aurakai.auraframefx.system.homescreen

/**
 * Enum defining different visual transition types for the home screen
 */
enum class HomeScreenTransitionType {
    DIGITAL_DECONSTRUCT,
    HOLOGRAPHIC_FADE,
    CIRCUIT_PULSE,
    NEURAL_WIPE,
    NEON_SLIDE;

    companion object {
        fun getDefault(): HomeScreenTransitionType = DIGITAL_DECONSTRUCT
    }
}
