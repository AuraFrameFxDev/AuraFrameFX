package com.example.app.system.homescreen

import com.example.app.system.overlay.*
import kotlinx.serialization.Serializable

@Serializable
data class HomeScreenTransitionConfig(
    val type: HomeScreenTransitionType,
    val duration: Int = 500,
    val easing: String = "easeInOut",
    val properties: Map<String, Any> = emptyMap(),
)

@Serializable
data class HomeScreenTransitionEffect(
    val id: String,
    val type: HomeScreenTransitionType,
    val properties: Map<String, Any>,
)

enum class HomeScreenTransitionType {
    // Basic Transitions
    SLIDE_LEFT,
    SLIDE_RIGHT,
    SLIDE_UP,
    SLIDE_DOWN,
    FADE_IN,
    FADE_OUT,
    SCALE_IN,
    SCALE_OUT,
    ROTATE_IN,
    ROTATE_OUT,

    // Card Stack Transitions
    STACK_SLIDE,
    STACK_FADE,
    STACK_SCALE,
    STACK_ROTATE,
    STACK_SHUFFLE,
    STACK_WAVE,
    STACK_PULSE,
    STACK_GLOW,

    // 3D Transitions
    STACK_ROTATE_3D,
    STACK_SCALE_3D,
    STACK_SLIDE_3D,
    STACK_WAVE_3D,
    STACK_PULSE_3D,
    STACK_GLOW_3D,

    // Globe Transitions
    GLOBE_ROTATE,
    GLOBE_SCALE,
    GLOBE_PULSE,
    GLOBE_GLOW,
    GLOBE_WAVE,
    GLOBE_SHUFFLE,

    // Fan Transitions
    FAN_IN,
    FAN_OUT,
    FAN_ROTATE,
    FAN_SCALE,
    FAN_WAVE,
    FAN_PULSE,
    FAN_GLOW,

    // Spread Transitions
    SPREAD_IN,
    SPREAD_OUT,
    SPREAD_ROTATE,
    SPREAD_SCALE,
    SPREAD_WAVE,
    SPREAD_PULSE,
    SPREAD_GLOW,

    // Digital/Deconstruct/Hologram Transitions
    DIGITAL_DECONSTRUCT,
    DIGITAL_RECONSTRUCT,
    HOLOGRAM_FORMING,

    // Custom Transitions
    CUSTOM_1,
    CUSTOM_2,
    CUSTOM_3,
    CUSTOM_4,
    CUSTOM_5
}

@Serializable
data class TransitionProperties(
    val angle: Float = 0f,
    val scale: Float = 1f,
    val offset: Float = 0f,
    val amplitude: Float = 0f,
    val frequency: Float = 0f,
    val color: String = "#00FFCC",
    val blur: Float = 0f,
    val spread: Float = 0f,
)
