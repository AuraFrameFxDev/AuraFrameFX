package dev.aurakai.auraframefx.model

/**
 * Sealed class representing different states of a conversation with AI agents
 */
sealed class ConversationState {
    object Idle : ConversationState()

    object Listening : ConversationState()

    data class Processing(
        val text: String = "",
        val progress: Float = 0f,
    ) : ConversationState()

    data class Responding(
        val text: String = "",
        val isComplete: Boolean = false,
    ) : ConversationState()

    data class Error(
        val message: String,
        val code: Int = -1,
    ) : ConversationState()

    data class Thinking(
        val topic: String = "",
        val progress: Float = 0f,
    ) : ConversationState()

    data class Interrupted(
        val reason: String = "",
    ) : ConversationState()
}
