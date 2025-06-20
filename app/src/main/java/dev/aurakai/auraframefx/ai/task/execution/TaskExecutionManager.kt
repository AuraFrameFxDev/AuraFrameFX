package dev.aurakai.auraframefx.ai.task.execution

import android.content.Context
import dev.aurakai.auraframefx.ai.services.KaiAIService
import android.content.Context
import dev.aurakai.auraframefx.ai.services.KaiAIService
import dev.aurakai.auraframefx.ai.task.Task
import dev.aurakai.auraframefx.data.logging.AuraFxLogger
import dev.aurakai.auraframefx.model.AgentType
import kotlinx.datetime.Clock // Added import
import kotlinx.datetime.Instant // Added import
import kotlinx.coroutines.flow.MutableStateFlow // Added import
import kotlinx.coroutines.flow.StateFlow // Added import
import kotlinx.coroutines.flow.update // Added import
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
public class TaskExecutionManager @Inject constructor(
    private val context: Context,
    private val kaiService: KaiAIService,
    private val auraFxLogger: AuraFxLogger,
) {
    private val _executions = MutableStateFlow<Map<String, TaskExecution>>(emptyMap()) // Added type for emptyMap
    public val executions: StateFlow<Map<String, TaskExecution>> = _executions

    private val _executionStats = MutableStateFlow(ExecutionStats())
    public val executionStats: StateFlow<ExecutionStats> = _executionStats

    private val _activeExecutions = mutableMapOf<String, TaskExecution>()
    private val _completedExecutions = mutableMapOf<String, TaskExecution>()
    private val _failedExecutions = mutableMapOf<String, TaskExecution>()

    public fun startExecution(task: Task, agent: AgentType): TaskExecution {
        public val executionPlan = createExecutionPlan(task)
        public val execution = TaskExecution(
            taskId = task.id,
            agent = agent,
            executionPlan = executionPlan
        )

        _executions.update { current ->
            current + (execution.id to execution)
        }
        _activeExecutions[execution.id] = execution

        updateStats(execution)
        return execution
    }

    private fun createExecutionPlan(task: Task): ExecutionPlan {
        public val steps = listOf(
            ExecutionStep(
                description = "Initialize task",
                type = StepType.INITIALIZATION,
                priority = 1.0f,
                estimatedDuration = 1000
            ),
            ExecutionStep(
                description = "Process context",
                type = StepType.CONTEXT,
                priority = 0.9f,
                estimatedDuration = 2000
            ),
            ExecutionStep(
                description = "Execute main task",
                type = StepType.COMPUTATION,
                priority = 0.8f,
                estimatedDuration = task.estimatedDuration
            ),
            ExecutionStep(
                description = "Finalize execution",
                type = StepType.FINALIZATION,
                priority = 0.7f,
                estimatedDuration = 1000
            )
        )

        return ExecutionPlan(
            steps = steps,
            estimatedDuration = steps.sumOf { it.estimatedDuration },
            requiredResources = setOf("cpu", "memory", "network")
        )
    }

    public fun updateExecutionProgress(executionId: String, progress: Float) {
        public val execution = _activeExecutions[executionId] ?: return
        public val updatedExecution = execution.copy(progress = progress)

        _executions.update { current ->
            current + (executionId to updatedExecution)
        }
        updateStats(updatedExecution)
    }

    public fun updateCheckpoint(executionId: String, stepId: String, status: CheckpointStatus) {
        public val execution = _activeExecutions[executionId] ?: return
        public val checkpoint = Checkpoint(
            stepId = stepId,
            status = status
        )

        public val updatedExecution = execution.copy(
            checkpoints = execution.checkpoints + checkpoint
        )

        _executions.update { current ->
            current + (executionId to updatedExecution)
        }
        updateStats(updatedExecution)
    }

    public fun completeExecution(executionId: String, result: ExecutionResult) {
        public val execution = _activeExecutions[executionId] ?: return
        public val updatedExecution = execution.copy(
            status = ExecutionStatus.COMPLETED,
            endTime = Clock.System.now().toEpochMilliseconds(), // Corrected Instant usage
            result = result
        )

        _activeExecutions.remove(executionId)
        _completedExecutions[executionId] = updatedExecution

        _executions.update { current ->
            current + (executionId to updatedExecution)
        }
        updateStats(updatedExecution)
    }

    public fun failExecution(executionId: String, error: Throwable) {
        public val execution = _activeExecutions[executionId] ?: return
        public val updatedExecution = execution.copy(
            status = ExecutionStatus.FAILED,
            endTime = Clock.System.now().toEpochMilliseconds(), // Corrected Instant usage
            result = ExecutionResult.FAILURE
        )

        _activeExecutions.remove(executionId)
        _failedExecutions[executionId] = updatedExecution

        _executions.update { current ->
            current + (executionId to updatedExecution)
        }
        updateStats(updatedExecution)
    }

    private fun updateStats(execution: TaskExecution) {
        _executionStats.update { current ->
            current.copy(
                totalExecutions = current.totalExecutions + 1,
                activeExecutions = _activeExecutions.size,
                completedExecutions = _completedExecutions.size,
                failedExecutions = _failedExecutions.size,
                lastUpdated = Clock.System.now().toEpochMilliseconds(), // Corrected Instant usage
                executionTimes = current.executionTimes + (execution.status to (current.executionTimes[execution.status]
                    ?: 0) + 1)
            )
        }
    }
}

public data class ExecutionStats(
    public val totalExecutions: Int = 0,
    public val activeExecutions: Int = 0,
    public val completedExecutions: Int = 0,
    public val failedExecutions: Int = 0,
    public val executionTimes: Map<ExecutionStatus, Int> = emptyMap(),
    public val lastUpdated: Long = Clock.System.now().toEpochMilliseconds(), // Corrected Instant usage
)
