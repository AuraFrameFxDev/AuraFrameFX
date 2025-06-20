package dev.aurakai.auraframefx.ai.task

import dev.aurakai.auraframefx.model.AgentType
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
public data class Task(
    public val id: String = "task_${Clock.System.now().toEpochMilliseconds()}",
    public val timestamp: Instant = Clock.System.now(),
    public val priority: TaskPriority = TaskPriority.NORMAL,
    public val urgency: TaskUrgency = TaskUrgency.MEDIUM,
    public val importance: TaskImportance = TaskImportance.MEDIUM,
    public val context: String,
    public val content: String,
    public val metadata: Map<String, String> = emptyMap(),
    public val status: TaskStatus = TaskStatus.PENDING,
    public val assignedAgents: Set<AgentType> = emptySet(),
    public val requiredAgents: Set<AgentType> = emptySet(),
    public val completionTime: Instant? = null,
    public val estimatedDuration: Long = 0,
    public val dependencies: Set<String> = emptySet(),
)

@Serializable
public data class TaskDependency(
    public val taskId: String,
    public val dependencyId: String,
    public val type: DependencyType,
    public val priority: TaskPriority,
    public val metadata: Map<String, String> = emptyMap(),
)

@Serializable
public data class TaskPriority(
    public val value: Float,
    public val reason: String,
    public val metadata: Map<String, String> = emptyMap(),
) {
    public companion object {
        public val CRITICAL = TaskPriority(1.0f, "Critical system task")
        public val HIGH = TaskPriority(0.8f, "High priority task")
        public val NORMAL = TaskPriority(0.5f, "Normal priority task")
        public val LOW = TaskPriority(0.3f, "Low priority background task")
        public val MINOR = TaskPriority(0.1f, "Minor maintenance task")
    }
}

@Serializable
public data class TaskUrgency(
    public val value: Float,
    public val reason: String,
    public val metadata: Map<String, String> = emptyMap(),
) {
    public companion object {
        public val IMMEDIATE = TaskUrgency(1.0f, "Immediate attention required")
        public val HIGH = TaskUrgency(0.8f, "High urgency")
        public val NORMAL = TaskUrgency(0.5f, "Normal urgency")
        public val LOW = TaskUrgency(0.3f, "Low urgency")
        public val BACKGROUND = TaskUrgency(0.1f, "Background task")
        public val MEDIUM = NORMAL
    }
}

@Serializable
public data class TaskImportance(
    public val value: Float,
    public val reason: String,
    public val metadata: Map<String, String> = emptyMap(),
) {
    public companion object {
        public val CRITICAL = TaskImportance(1.0f, "Critical importance")
        public val HIGH = TaskImportance(0.8f, "High importance")
        public val NORMAL = TaskImportance(0.5f, "Normal importance")
        public val LOW = TaskImportance(0.3f, "Low importance")
        public val MINOR = TaskImportance(0.1f, "Minor importance")
        public val MEDIUM = NORMAL
    }
}

@Serializable // Added annotation
public enum class TaskStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    FAILED,
    CANCELLED,
    BLOCKED,
    WAITING
}

@Serializable // Added annotation
public enum class DependencyType {
    BLOCKING,
    SEQUENTIAL,
    PARALLEL,
    OPTIONAL,
    SOFT
}
