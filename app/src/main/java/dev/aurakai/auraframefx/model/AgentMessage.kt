package dev.aurakai.auraframefx.model

import kotlinx.serialization.Serializable
import dev.aurakai.auraframefx.model.AgentType // Added import

@Serializable
public data class AgentMessage(
    public val content: String,
    public val sender: AgentType, // Should now refer to the imported AgentType
    public val timestamp: Long,
    public val confidence: Float,
)

// Removed local AgentType enum
