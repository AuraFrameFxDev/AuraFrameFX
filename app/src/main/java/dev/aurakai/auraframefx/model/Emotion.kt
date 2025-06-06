package dev.aurakai.auraframefx.model

/**
 * Enum representing the different emotional states that can be detected or conveyed by the system
 */
enum class Emotion {
    NEUTRAL,
    HAPPY,
    SAD,
    ANGRY,
    FEARFUL,
    SURPRISED,
    DISGUSTED,
    CALM,
    EXCITED,
    THOUGHTFUL,
    CONFUSED;
    
    companion object {
        fun fromString(value: String, default: Emotion = NEUTRAL): Emotion {
            return try {
                valueOf(value.uppercase())
            } catch (e: IllegalArgumentException) {
                default
            }
        }
    }
}
