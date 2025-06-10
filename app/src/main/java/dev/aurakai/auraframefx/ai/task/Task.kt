package dev.aurakai.auraframefx.ai.task

import kotlinx.serialization.Serializable
import java.util.UUID // For default ID generation if needed, though ID is String

@Serializable
data class Task(
    val id: String = UUID.randomUUID().toString(), // Default to a random UUID if not provided
    val type: TaskType,
    val parameters: Map<String, String> = emptyMap(),
    val schedule: TaskSchedule? = null,
    val priority: Int = 5, // 1 (highest) to 10 (lowest)
    val requestedBy: String = "AI_System", // E.g., "Kai", "Aura", "User", "SystemMonitor"
)

@Serializable
enum class TaskType {
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
data class TaskSchedule(
    val type: String = "immediate", // "immediate", "at_time", "periodic", "on_event"
    val delayMs: Long = 0, // Delay before execution for "immediate" if > 0
    val intervalMs: Long? = null, // For "periodic" tasks
    val targetTimeMillis: Long? = null, // For "at_time" tasks
)
