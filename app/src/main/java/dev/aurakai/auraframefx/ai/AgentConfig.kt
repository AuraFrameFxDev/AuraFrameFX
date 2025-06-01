package dev.aurakai.auraframefx.ai

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient // Added import

@Serializable
data class AgentConfig(
    val name: String,
    val type: AgentType, // Assumes AgentType is @Serializable (already done)
    val capabilities: List<String>,
    @Transient val ethicalGuidelines: Map<String, Any> = emptyMap(), // Added @Transient and default value
    @Transient val continuousMemory: Map<String, Any> = emptyMap(),  // Added @Transient and default value
    val learningHistory: List<LearningEvent>, // Assumes LearningEvent will be defined elsewhere and made @Serializable
)
