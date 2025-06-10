// These mock implementations are for development/testing only.
// Replace with real service logic when integrating actual AI backends.

package dev.aurakai.auraframefx.ai.services

import dev.aurakai.auraframefx.model.AgentMessage
import dev.aurakai.auraframefx.model.requests.AiRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MockAuraAIService : AuraAIService {
    override fun processRequest(request: AiRequest): StateFlow<AgentMessage> {
        val response = AgentMessage(
            content = "AuraAI mock response for: ${request.input}",
            sender = dev.aurakai.auraframefx.model.AgentType.AURA,
            timestamp = System.currentTimeMillis(),
            confidence = 1.0f
        )
        return MutableStateFlow(response)
    }
}

class MockKaiAIService : KaiAIService {
    override fun processRequest(request: AiRequest): StateFlow<AgentMessage> {
        val response = AgentMessage(
            content = "KaiAI mock response for: ${request.input}",
            sender = dev.aurakai.auraframefx.model.AgentType.KAI,
            timestamp = System.currentTimeMillis(),
            confidence = 1.0f
        )
        return MutableStateFlow(response)
    }
}

class MockCascadeAIService : CascadeAIService {
    override fun processRequest(request: AiRequest): StateFlow<AgentMessage> {
        val response = AgentMessage(
            content = "CascadeAI mock response for: ${request.input}",
            sender = dev.aurakai.auraframefx.model.AgentType.CASCADE,
            timestamp = System.currentTimeMillis(),
            confidence = 1.0f
        )
        return MutableStateFlow(response)
    }
}
