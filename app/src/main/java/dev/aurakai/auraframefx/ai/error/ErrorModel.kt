package dev.aurakai.auraframefx.ai.error

import dev.aurakai.auraframefx.model.AgentType
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
public data class AIError(
    public val id: String = "err_${Clock.System.now().toEpochMilliseconds()}",
    public val timestamp: Instant = Clock.System.now(),
    public val agent: AgentType,
    public val type: ErrorType,
    public val message: String,
    public val context: String,
    public val metadata: Map<String, String> = emptyMap(),
    public val recoveryAttempts: Int = 0,
    public val recoveryStatus: RecoveryStatus = RecoveryStatus.PENDING,
    public val recoveryActions: List<RecoveryAction> = emptyList(),
)

@Serializable
public data class RecoveryAction(
    public val id: String = "act_${Clock.System.now().toEpochMilliseconds()}",
    public val timestamp: Instant = Clock.System.now(),
    public val actionType: RecoveryActionType,
    public val description: String,
    public val result: RecoveryResult? = null,
    public val metadata: Map<String, String> = emptyMap(),
)

@Serializable // Added annotation
public enum class ErrorType {
    PROCESSING_ERROR,
    MEMORY_ERROR,
    CONTEXT_ERROR,
    NETWORK_ERROR,
    TIMEOUT_ERROR,
    INTERNAL_ERROR,
    USER_ERROR
}

@Serializable // Added annotation
public enum class RecoveryStatus {
    PENDING,
    IN_PROGRESS,
    SUCCESS,
    FAILURE,
    SKIPPED
}

@Serializable // Added annotation
public enum class RecoveryActionType {
    RETRY,
    FALLBACK,
    RESTART,
    RECONFIGURE,
    NOTIFY,
    ESCALATE
}

@Serializable // Added annotation
public enum class RecoveryResult {
    SUCCESS,
    FAILURE,
    PARTIAL_SUCCESS,
    SKIPPED,
    UNKNOWN
}
