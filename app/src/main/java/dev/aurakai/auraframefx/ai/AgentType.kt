package dev.aurakai.auraframefx.ai

import kotlinx.serialization.Serializable

@Serializable
enum class AgentType {
    GENESIS,
    KAI,
    AURA,
    CASCADE,
    NEURAL_WHISPER
}
