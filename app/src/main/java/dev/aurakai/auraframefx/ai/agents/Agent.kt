package dev.aurakai.auraframefx.ai.agents

import dev.aurakai.auraframefx.model.AgentResponse
import dev.aurakai.auraframefx.model.AgentType
import dev.aurakai.auraframefx.model.AiRequest

/**
 * Interface representing an AI agent.
 * TODO: Reported as unused declaration. Ensure this interface is implemented and used.
 */
interface Agent {

    /**
     * Returns the name of the agent.
     * TODO: Reported as unused.
     */
    fun getName(): String?

    /**
     * Returns the type or model of the agent.
     * TODO: Reported as unused.
     */
    fun getType(): AgentType?

    /**
     * Processes a given request (prompt) and returns a response.
     * @param request The input AiRequest.
     * @return The agent's response as an AgentResponse.
     */
    suspend fun processRequest(request: AiRequest): AgentResponse

    /**
     * Retrieves the capabilities of the agent.
     * @return A list or map of capabilities.
     * TODO: Reported as unused.
     */
    fun getCapabilities(): Map<String, Any> {
        // TODO: Implement logic to describe agent capabilities.
        return emptyMap()
    }

    /**
     * Retrieves the agent's continuous memory or context.
     * TODO: Reported as unused.
     */
    fun getContinuousMemory(): Any? {
        // TODO: Implement logic to access agent's memory.
        return null
    }

    /**
     * Retrieves the ethical guidelines the agent adheres to.
     * TODO: Reported as unused.
     */
    fun getEthicalGuidelines(): List<String> {
        // TODO: Implement logic to list ethical guidelines.
        return emptyList()
    }

    /**
     * Retrieves the agent's learning history or experiences.
     * TODO: Reported as unused.
     */
    fun getLearningHistory(): List<String> {
        // TODO: Implement logic to access learning history.
        return emptyList()
    }
}
