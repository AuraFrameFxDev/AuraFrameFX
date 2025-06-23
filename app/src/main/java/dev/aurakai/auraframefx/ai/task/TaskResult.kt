package dev.aurakai.auraframefx.ai.task

import kotlinx.serialization.Serializable

@Serializable
data class TaskResult(
    val taskId: String,
    val status: TaskStatus,
    val message: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val durationMs: Long? = null, // How long the task took
) {
    companion object {
        fun success(taskId: String, message: String? = null, durationMs: Long? = null) =
            TaskResult(taskId, TaskStatus.COMPLETED, message, System.currentTimeMillis(), durationMs)
            
        fun failed(taskId: String, message: String? = null, durationMs: Long? = null) =
            TaskResult(taskId, TaskStatus.FAILED, message, System.currentTimeMillis(), durationMs)
    }
}
