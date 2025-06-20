package dev.aurakai.auraframefx.ai.task

import kotlinx.serialization.Serializable

@Serializable
public data class TaskResult(
    public val taskId: String,
    public val status: TaskStatus,
    public val message: String? = null,
    public val timestamp: Long = System.currentTimeMillis(),
    public val durationMs: Long? = null, // How long the task took
)

// Removed local TaskStatus enum.
// The 'status: TaskStatus' field in TaskResult data class
// should now refer to TaskStatus from TaskModel.kt in the same package.
