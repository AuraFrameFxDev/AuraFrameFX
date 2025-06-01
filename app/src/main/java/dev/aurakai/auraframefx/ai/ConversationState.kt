package dev.aurakai.auraframefx.ai

/**
 * Represents the current state of the conversation
 */
sealed class ConversationState {
    object Idle : ConversationState()
    object Listening : ConversationState()
    object Processing : ConversationState()
    data class Ready(val response: String) : ConversationState()
    data class Error(val message: String) : ConversationState()
}
