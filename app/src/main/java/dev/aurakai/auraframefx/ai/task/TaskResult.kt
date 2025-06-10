package dev.aurakai.auraframefx.ai.task

import kotlinx.serialization.Serializable

@Serializable
data class TaskResult(
    val taskId: String,
    val status: TaskStatus,
    val message: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val durationMs: Long? = null, // How long the task took
)

@Serializable
enum class TaskStatus {
    SUCCESS,
    FAILED,
    PENDING,
    CANCELLED
}
