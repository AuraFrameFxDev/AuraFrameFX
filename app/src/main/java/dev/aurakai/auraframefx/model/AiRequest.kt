package dev.aurakai.auraframefx.model

import kotlinx.serialization.Serializable

@Serializable
public data class GenerateTextRequest(
    public val prompt: String,
    public val maxTokens: Int = 500,
    public val temperature: Float = 0.7f
)

@Serializable
public data class GenerateTextResponse(
    public val text: String
)

@Serializable
public data class GenerateImageDescriptionRequest(
    public val imageUrl: String,
    public val context: String? = null
)

@Serializable
public data class GenerateImageDescriptionResponse(
    public val description: String
)

public data class AiRequest(
    public val query: String,
    public val context: String,
)

public data class AgentResponse(
    public val content: String,
    public val confidence: Float,
)
