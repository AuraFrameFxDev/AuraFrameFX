package dev.aurakai.auraframefx.model

import kotlinx.serialization.Serializable

@Serializable
data class GenerateTextRequest(
    val prompt: String,
    val maxTokens: Int = 500,
    val temperature: Float = 0.7f,
)

@Serializable
data class GenerateTextResponse(
    val text: String,
)

@Serializable
data class GenerateImageDescriptionRequest(
    val imageUrl: String,
    val context: String? = null,
)

@Serializable
data class GenerateImageDescriptionResponse(
    val description: String,
)

data class AiRequest(
    val query: String,
    val context: String,
)

data class AgentResponse(
    val content: String,
    val confidence: Float,
)
