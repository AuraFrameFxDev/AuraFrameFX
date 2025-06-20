package dev.aurakai.auraframefx.ai.task.execution

import dev.aurakai.auraframefx.model.AgentType
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
public data class TaskExecution(
    public val id: String = "exec_${Clock.System.now().toEpochMilliseconds()}",
    public val taskId: String,
    public val agent: AgentType,
    public val startTime: Instant = Clock.System.now(),
    public val endTime: Instant? = null,
    public val status: ExecutionStatus = ExecutionStatus.PENDING,
    public val progress: Float = 0.0f,
    public val result: ExecutionResult? = null,
    public val metadata: Map<String, String> = emptyMap(),
    public val executionPlan: ExecutionPlan? = null,
    public val checkpoints: List<Checkpoint> = emptyList(),
)

@Serializable
public data class ExecutionPlan(
    public val id: String = "plan_${Clock.System.now().toEpochMilliseconds()}",
    public val steps: List<ExecutionStep>,
    public val estimatedDuration: Long,
    public val requiredResources: Set<String>,
    public val metadata: Map<String, String> = emptyMap(),
)

@Serializable
public data class ExecutionStep(
    public val id: String = "step_${Clock.System.now().toEpochMilliseconds()}",
    public val description: String,
    public val type: StepType,
    public val priority: Float = 0.5f,
    public val estimatedDuration: Long = 0,
    public val dependencies: Set<String> = emptySet(),
    public val metadata: Map<String, String> = emptyMap(),
)

@Serializable
public data class Checkpoint(
    public val id: String = "chk_${Clock.System.now().toEpochMilliseconds()}",
    public val timestamp: Instant = Clock.System.now(),
    public val stepId: String,
    public val status: CheckpointStatus,
    public val progress: Float = 0.0f,
    public val metadata: Map<String, String> = emptyMap(),
)

@Serializable // Added annotation
public enum class ExecutionStatus {
    PENDING,
    INITIALIZING,
    RUNNING,
    PAUSED,
    COMPLETED,
    FAILED,
    CANCELLED,
    TIMEOUT
}

@Serializable // Added annotation
public enum class ExecutionResult {
    SUCCESS,
    PARTIAL_SUCCESS,
    FAILURE,
    CANCELLED,
    TIMEOUT,
    UNKNOWN
}

@Serializable // Added annotation
public enum class StepType {
    COMPUTATION,
    COMMUNICATION,
    MEMORY,
    CONTEXT,
    DECISION,
    ACTION,
    MONITORING,
    REPORTING
}

@Serializable // Added annotation
public enum class CheckpointStatus {
    PENDING,
    STARTED,
    COMPLETED,
    FAILED,
    SKIPPED
}
