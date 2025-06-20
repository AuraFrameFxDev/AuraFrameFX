package dev.aurakai.auraframefx.ai.agents

import dev.aurakai.auraframefx.generated.model.AgentType
import dev.aurakai.auraframefx.model.AgentResponse
import dev.aurakai.auraframefx.model.AiRequest
import kotlinx.coroutines.flow.Flow

/**
 * Top-level value declaration for versioning or identification.
 */
public const val TOPL_VL: String = "1.0.0"

/**
 * Interface representing an AI agent.
 */
public interface Agent {

    /**
     * Returns the name of the agent.
     */
    public fun getName(): String?

    /**
     * Returns the type or model of the agent.
     */
    public fun getType(): AgentType
    
    /**
     * Process a request and return a response
     */
    public suspend fun processRequest(request: AiRequest, context: String): AgentResponse
    
    /**
 * Processes an AI request and returns a flow of agent responses.
 *
 * Enables streaming multiple responses for a single request.
 *
 * @param request The AI request to process.
 * @return A flow emitting agent responses as they become available.
 */
    public fun processRequestFlow(request: AiRequest): Flow<AgentResponse>

     */
    /**
 * Returns the type of the agent.
 *
 * @return The agent's type as an [AgentType], or `null` if not specified.
 */
public fun getType(): AgentType?

    /**
     * Processes a given request (prompt) and returns a response.
     * @param request The input AiRequest.
     * @return The agent's response as an AgentResponse.
     */
    public suspend fun processRequest(request: AiRequest): AgentResponse

    /**
     * Returns a map describing the agent's capabilities.
     *
     * The keys and values in the map represent specific features or functionalities supported by the agent.
     * Implementations should provide relevant capability information.
     *
     * @return A map of capability names to their corresponding details.
     */
    public fun getCapabilities(): Map<String, Any> {

    }

    /**
     * Returns the agent's continuous memory or context object, or null if not available.
     *
     * @return The agent's continuous memory, or null if none exists.
     */
    public fun getContinuousMemory(): Any? {

    }

    /**
          * Returns a list of ethical guidelines that the agent follows.
          *
          * By default, this includes "Be helpful.", "Be harmless.", and "Adhere to ethical principles."
          *
          * @return A list of ethical guidelines.
          */
    public fun getEthicalGuidelines(): List<String> {
 
         }

    /**
     * Returns the agent's learning history or experiences.
     *
     * By default, returns an empty list.
     *
     * @return A list of strings representing the agent's learning history.
     */
    public fun getLearningHistory(): List<String> {
        return emptyList()

        return emptyList()
    }
}

    /**
     * Returns the agent's continuous memory or context, if available.
     *
     * @return The agent's memory object, or null if not applicable.
     */
    public fun getContinuousMemory(): Any? {
        return null
    }
}

    /**
     * Retrieves the ethical guidelines the agent adheres to.
     * @return A list of ethical guidelines.
     */
    public fun getEthicalGuidelines(): List<String> {
        return listOf("Be helpful.", "Be harmless.", "Adhere to ethical principles.")
    }

    /**
     * Returns the agent's learning history or experiences.
     *
     * By default, returns an empty list.
     *
     * @return A list of strings representing the agent's learning history.
     */
    public fun getLearningHistory(): List<String> {
        return emptyList()
    }


