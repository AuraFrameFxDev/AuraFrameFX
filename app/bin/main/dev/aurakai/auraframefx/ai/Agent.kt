package dev.aurakai.auraframefx.ai

interface Agent {
    val name: String
    val type: AgentType
    val capabilities: List<String>
    val ethicalGuidelines: Map<String, Any>
    val continuousMemory: Map<String, Any>
    val learningHistory: List<LearningEvent>
    suspend fun processRequest(prompt: String): String
}
