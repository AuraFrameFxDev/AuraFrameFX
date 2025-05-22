package dev.aurakai.auraframefx.ai

data class AgentConfig(
    val name: String,
    val type: AgentType,
    val capabilities: List<String>,
    val ethicalGuidelines: Map<String, Any>,
    val continuousMemory: Map<String, Any>,
    val learningHistory: List<LearningEvent>,
)
