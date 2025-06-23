package dev.aurakai.auraframefx.ai.task.execution

import dev.aurakai.auraframefx.model.AgentType
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class TaskExecution(
    val id: String = "exec_${Clock.System.now().toEpochMilliseconds()}",
    val taskId: String,
    val agent: AgentType,
    val startTime: Instant = Clock.System.now(),
    val endTime: Instant? = null,
    val status: ExecutionStatus = ExecutionStatus.PENDING,
    val progress: Float = 0.0f,
    val result: ExecutionResult? = null,
    @Contextual
    val metadata: Map<String, @Contextual Any> = emptyMap(),
    val executionPlan: ExecutionPlan? = null,
    val checkpoints: List<Checkpoint> = emptyList(),
)

@Serializable
data class ExecutionPlan(
    val id: String = "plan_${Clock.System.now().toEpochMilliseconds()}",
    val steps: List<ExecutionStep>,
    val estimatedDuration: Long,
    val requiredResources: Set<String>,
    @Contextual
    val metadata: Map<String, @Contextual Any> = emptyMap(),
)

@Serializable
data class ExecutionStep(
    val id: String = "step_${Clock.System.now().toEpochMilliseconds()}",
    val description: String,
    val type: StepType,
    val priority: Float = 0.5f,
    val estimatedDuration: Long = 0,
    val dependencies: Set<String> = emptySet(),
    @Contextual
    val metadata: Map<String, @Contextual Any> = emptyMap(),
)

@Serializable
data class Checkpoint(
    val id: String = "chk_${Clock.System.now().toEpochMilliseconds()}",
    val timestamp: Instant = Clock.System.now(),
    val stepId: String,
    val status: CheckpointStatus,
    val progress: Float = 0.0f,
    @Contextual
    val metadata: Map<String, @Contextual Any> = emptyMap(),
)

@Serializable
enum class ExecutionStatus {
    PENDING,
    INITIALIZING,
    RUNNING,
    PAUSED,
    COMPLETED,
    FAILED,
    CANCELLED,
    TIMEOUT
}

@Serializable
enum class ExecutionResult {
    SUCCESS,
    PARTIAL_SUCCESS,
    FAILURE,
    CANCELLED,
    TIMEOUT,
    UNKNOWN
}

@Serializable
enum class StepType {
    COMPUTATION,
    COMMUNICATION,
    MEMORY,
    CONTEXT,
    DECISION,
    ACTION,
    MONITORING,
    REPORTING
}

@Serializable
enum class CheckpointStatus {
    PENDING,
    STARTED,
    COMPLETED,
    FAILED,
    SKIPPED
}
