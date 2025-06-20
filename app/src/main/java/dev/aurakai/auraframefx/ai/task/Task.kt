package dev.aurakai.auraframefx.ai.task

import kotlinx.serialization.Serializable

// Task data class definition removed as per instruction.

@Serializable
public enum class TaskType {
    TOGGLE_WIFI,
    ADJUST_BRIGHTNESS,
    OPTIMIZE_BATTERY,
    RUN_SECURITY_SCAN,
    RUN_SCRIPT,
    UPDATE_CONFIG,
    SEND_NOTIFICATION,

    // Add more task types as needed
    UNKNOWN
}

@Serializable
public data class TaskSchedule(
    public val type: String = "immediate", // "immediate", "at_time", "periodic", "on_event"
    public val delayMs: Long = 0, // Delay before execution for "immediate" if > 0
    public val intervalMs: Long? = null, // For "periodic" tasks
    public val targetTimeMillis: Long? = null, // For "at_time" tasks
)
