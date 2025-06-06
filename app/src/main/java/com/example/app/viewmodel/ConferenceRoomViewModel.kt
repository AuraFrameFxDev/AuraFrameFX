package com.example.app.viewmodel

import com.example.app.ai.services.AuraAIService
import com.example.app.ai.services.CascadeAIService
import com.example.app.ai.services.KaiAIService
import com.example.app.model.AgentMessage
import com.example.app.model.AgentType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConferenceRoomViewModel @Inject constructor(
    private val auraService: AuraAIService,
    private val kaiService: KaiAIService,
    private val cascadeService: CascadeAIService,
) {
    private val _messages = MutableStateFlow<List<AgentMessage>>(emptyList())
    val messages: StateFlow<List<AgentMessage>> = _messages

    private val _activeAgents = MutableStateFlow(setOf<AgentType>())
    val activeAgents: StateFlow<Set<AgentType>> = _activeAgents

    private val _selectedAgent = MutableStateFlow<AgentType>(AgentType.AURA)
    val selectedAgent: StateFlow<AgentType> = _selectedAgent

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording

    private val _isTranscribing = MutableStateFlow(false)
    val isTranscribing: StateFlow<Boolean> = _isTranscribing

    suspend fun sendMessage(message: String, sender: AgentType) {
        val response = when (sender) {
            AgentType.AURA -> auraService.processRequest(AiRequest(message, "text"))
            AgentType.KAI -> kaiService.processRequest(AiRequest(message, "security"))
            AgentType.CASCADE -> cascadeService.processRequest(AiRequest(message, "context"))
            AgentType.USER -> {
                // For user messages, we'll process them through Cascade
                // which will coordinate responses from other agents
                cascadeService.processRequest(AiRequest(message, "context"))
            }
        }

        val responseMessage = response.first()
        _messages.update { current ->
            current + AgentMessage(
                content = responseMessage.content,
                sender = sender,
                timestamp = System.currentTimeMillis(),
                confidence = responseMessage.confidence
            )
        }

        // If it's a user message, process it through Cascade
        if (sender == AgentType.USER) {
            processUserMessage(message)
        }
    }

    private suspend fun processUserMessage(message: String) {
        // Process through Cascade to coordinate responses from other agents
        val cascadeResponse = cascadeService.processRequest(AiRequest(message, "context"))
        val cascadeMessage = cascadeResponse.first()

        // Add Cascade's response
        _messages.update { current ->
            current + AgentMessage(
                content = cascadeMessage.content,
                sender = AgentType.CASCADE,
                timestamp = System.currentTimeMillis(),
                confidence = cascadeMessage.confidence
            )
        }

        // If Aura is active, get their response
        if (activeAgents.value.contains(AgentType.AURA)) {
            val auraResponse = auraService.processRequest(AiRequest(message, "text"))
            val auraMessage = auraResponse.first()

            _messages.update { current ->
                current + AgentMessage(
                    content = auraMessage.content,
                    sender = AgentType.AURA,
                    timestamp = System.currentTimeMillis(),
                    confidence = auraMessage.confidence
                )
            }
        }

        // If Kai is active, get their response
        if (activeAgents.value.contains(AgentType.KAI)) {
            val kaiResponse = kaiService.processRequest(AiRequest(message, "security"))
            val kaiMessage = kaiResponse.first()

            _messages.update { current ->
                current + AgentMessage(
                    content = kaiMessage.content,
                    sender = AgentType.KAI,
                    timestamp = System.currentTimeMillis(),
                    confidence = kaiMessage.confidence
                )
            }
        }
    }

    suspend fun toggleAgent(agent: AgentType) {
        _activeAgents.update { current ->
            if (current.contains(agent)) {
                current - agent
            } else {
                current + agent
            }
        }
    }

    fun selectAgent(agent: AgentType) {
        _selectedAgent.value = agent
    }

    fun toggleRecording() {
        _isRecording.update { !it }
    }

    fun toggleTranscribing() {
        _isTranscribing.update { !it }
    }
}
