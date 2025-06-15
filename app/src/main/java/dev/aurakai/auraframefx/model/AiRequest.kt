package dev.aurakai.auraframefx.model

data class AiRequest(
    val query: String,
    val context: String,
)

data class AgentResponse(
    val content: String,
    val confidence: Float,
)

