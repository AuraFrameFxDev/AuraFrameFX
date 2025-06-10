package dev.aurakai.auraframefx.model

import kotlinx.serialization.Serializable

@Serializable
data class AgentMessage(
    val content: String,
    val sender: AgentType,
    val timestamp: Long,
    val confidence: Float,
)

enum class AgentType {
    AURA,
    KAI,
    CASCADE,
    USER
}
