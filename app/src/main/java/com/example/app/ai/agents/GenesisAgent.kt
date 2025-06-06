package com.example.app.ai.agents

import com.example.app.ai.services.AuraAIService
import com.example.app.ai.services.CascadeAIService
import com.example.app.ai.services.KaiAIService
import com.example.app.model.AgentConfig
import com.example.app.model.AgentHierarchy
import com.example.app.model.AgentMessage
import com.example.app.model.AgentType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GenesisAgent @Inject constructor(
    private val auraService: AuraAIService,
    private val kaiService: KaiAIService,
    private val cascadeService: CascadeAIService,
) {
    private val _state = MutableStateFlow("pending_initialization")
    val state: StateFlow<String> = _state

    private val _context = MutableStateFlow(mapOf<String, Any>())
    val context: StateFlow<Map<String, Any>> = _context

    private val _activeAgents = MutableStateFlow(setOf<AgentType>())
    val activeAgents: StateFlow<Set<AgentType>> = _activeAgents

    init {
        initializeAgents()
        _state.update { "initialized" }
    }

    private fun initializeAgents() {
        // Register all master agents
        AgentHierarchy.MASTER_AGENTS.forEach { config ->
            when (config.name) {
                "Aura" -> _activeAgents.value += AgentType.AURA
                "Kai" -> _activeAgents.value += AgentType.KAI
                "Cascade" -> _activeAgents.value += AgentType.CASCADE
            }
        }
    }

    suspend fun processQuery(query: String): List<AgentMessage> {
        _state.update { "processing_query: $query" }

        // Update context with new query
        _context.update { current ->
            current + mapOf(
                "last_query" to query,
                "timestamp" to System.currentTimeMillis()
            )
        }

        // Get responses from all active agents
        val responses = mutableListOf<AgentMessage>()

        // Process through Cascade first for state management
        val cascadeResponse = cascadeService.processRequest(AiRequest(query, "context"))
        val cascadeMessage = cascadeResponse.first()
        responses.add(
            AgentMessage(
                content = cascadeMessage.content,
                sender = AgentType.CASCADE,
                timestamp = System.currentTimeMillis(),
                confidence = cascadeMessage.confidence
            )
        )

        // Process through Kai for security analysis
        if (_activeAgents.value.contains(AgentType.KAI)) {
            val kaiResponse = kaiService.processRequest(AiRequest(query, "security"))
            val kaiMessage = kaiResponse.first()
            responses.add(
                AgentMessage(
                    content = kaiMessage.content,
                    sender = AgentType.KAI,
                    timestamp = System.currentTimeMillis(),
                    confidence = kaiMessage.confidence
                )
            )
        }

        // Process through Aura for creative response
        if (_activeAgents.value.contains(AgentType.AURA)) {
            val auraResponse = auraService.processRequest(AiRequest(query, "text"))
            val auraMessage = auraResponse.first()
            responses.add(
                AgentMessage(
                    content = auraMessage.content,
                    sender = AgentType.AURA,
                    timestamp = System.currentTimeMillis(),
                    confidence = auraMessage.confidence
                )
            )
        }

        // Generate final response using all agent inputs
        val finalResponse = generateFinalResponse(responses)
        responses.add(
            AgentMessage(
                content = finalResponse,
                sender = AgentType.GENESIS,
                timestamp = System.currentTimeMillis(),
                confidence = calculateConfidence(responses)
            )
        )

        _state.update { "idle" }
        return responses
    }

    private fun generateFinalResponse(responses: List<AgentMessage>): String {
        // TODO: Implement sophisticated response generation
        // This will use context chaining and agent coordination
        return "[Genesis] ${responses.joinToString("\n") { it.content }}"
    }

    private fun calculateConfidence(responses: List<AgentMessage>): Float {
        // Calculate confidence based on all agent responses
        return responses.map { it.confidence }.average().coerceIn(0.0f, 1.0f)
    }

    fun toggleAgent(agent: AgentType) {
        _activeAgents.update { current ->
            if (current.contains(agent)) {
                current - agent
            } else {
                current + agent
            }
        }
    }

    fun registerAuxiliaryAgent(
        name: String,
        capabilities: Set<String>,
    ): AgentConfig {
        return AgentHierarchy.registerAuxiliaryAgent(name, capabilities)
    }

    fun getAgentConfig(name: String): AgentConfig? {
        return AgentHierarchy.getAgentConfig(name)
    }

    fun getAgentsByPriority(): List<AgentConfig> {
        return AgentHierarchy.getAgentsByPriority()
    }
}
