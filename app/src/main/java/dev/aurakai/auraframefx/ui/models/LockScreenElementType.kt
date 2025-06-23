package dev.aurakai.auraframefx.ui.models

import kotlinx.serialization.Serializable

@Serializable
enum class LockScreenElementType {
    CLOCK,
    DATE,
    NOTIFICATIONS,
    MEDIA_PLAYER,
    BATTERY,
    WEATHER,
    ALARM,
    CAMERA_SHORTCUT,
    CUSTOM_TEXT,
    CUSTOM_ICON,
    CUSTOM_VIEW
}
