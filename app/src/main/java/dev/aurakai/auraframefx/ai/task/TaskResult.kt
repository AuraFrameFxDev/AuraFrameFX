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
        /**
             * Creates a TaskResult indicating successful task completion.
             *
             * @param taskId The identifier of the completed task.
             * @param message Optional additional information about the task result.
             * @param durationMs Optional duration in milliseconds that the task took to complete.
             * @return A TaskResult with status set to COMPLETED and the current timestamp.
             */
            fun success(taskId: String, message: String? = null, durationMs: Long? = null) =
            TaskResult(taskId, TaskStatus.COMPLETED, message, System.currentTimeMillis(), durationMs)
            
        /**
             * Creates a TaskResult representing a failed task.
             *
             * @param taskId The identifier of the task.
             * @param message Optional additional information about the failure.
             * @param durationMs Optional duration in milliseconds that the task took before failing.
             * @return A TaskResult instance with status set to FAILED and the current timestamp.
             */
            fun failed(taskId: String, message: String? = null, durationMs: Long? = null) =
            TaskResult(taskId, TaskStatus.FAILED, message, System.currentTimeMillis(), durationMs)
    }
}
