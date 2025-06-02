package dev.aurakai.auraframefx.ai

class BaseAgent(
    override val name: String,
    override val type: AgentType,
    override val capabilities: List<String>,
    override val ethicalGuidelines: Map<String, Any>,
    override val continuousMemory: Map<String, Any>,
    override val learningHistory: List<LearningEvent>,
) : Agent {
    override suspend fun processRequest(prompt: String): String {
        // Basic implementation - should be overridden by specific agent implementations
        return "BaseAgent processing request: $prompt"
    }
}
