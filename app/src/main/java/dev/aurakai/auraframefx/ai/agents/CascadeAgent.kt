package dev.aurakai.auraframefx.ai.agents

import dev.aurakai.auraframefx.model.AgentType
import dev.aurakai.auraframefx.model.agent_states.ProcessingState
import dev.aurakai.auraframefx.model.agent_states.VisionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/**
 * CascadeAgent is responsible for stateful processing and vision management.
 * It acts as a bridge between Aura and Kai agents, maintaining context and state.
 */
@Singleton
class CascadeAgent @Inject constructor(
    private val auraAgent: AuraAgent,
    private val kaiAgent: KaiAgent,
) : BaseAgent("Cascade", "StatefulProcessor") {

    private val _visionState = MutableStateFlow(VisionState())
    val visionState: StateFlow<VisionState> = _visionState.asStateFlow()

    private val _processingState = MutableStateFlow(ProcessingState())
    val processingState: StateFlow<ProcessingState> = _processingState.asStateFlow()

    /**
     * Updates the vision state with new data.
     * @param newState The new vision state to set.
     */
    suspend fun updateVisionState(newState: VisionState) {
        _visionState.update { newState }
        // Notify Aura and Kai of vision changes
        auraAgent.onVisionUpdate(newState)
        kaiAgent.onVisionUpdate(newState)
    }

    /**
     * Updates the processing state.
     * @param newState The new processing state.
     */
    suspend fun updateProcessingState(newState: ProcessingState) {
        _processingState.update { newState }
        // Notify Aura and Kai of processing state changes
        auraAgent.onProcessingStateChange(newState)
        kaiAgent.onProcessingStateChange(newState)
    }

    /**
     * Processes a request with context awareness.
     * @param prompt The input prompt.
     * @return The processed response.
     */
    override suspend fun processRequest(prompt: String): String {
        visionState.value
        val currentProcessing = processingState.value

        // Determine which agent to delegate to based on context
        return when {
            kaiAgent.shouldHandleSecurity(prompt) -> {
                val response = kaiAgent.processRequest(prompt)
                updateProcessingState(currentProcessing.copy(lastAction = "Security Check"))
                response
            }

            auraAgent.shouldHandleCreative(prompt) -> {
                val response = auraAgent.processRequest(prompt)
                updateProcessingState(currentProcessing.copy(lastAction = "Creative Processing"))
                response
            }

            else -> {
                val response = "Cascade processing: $prompt"
                updateProcessingState(currentProcessing.copy(lastAction = "General Processing"))
                response
            }
        }
    }

    /**
     * Retrieves the current capabilities of the agent.
     */
    override fun getCapabilities(): Map<String, Any> {
        return mapOf(
            "agentName" to name,
            "agentType" to AgentType.CASCADING,
            "visionCapable" to true,
            "stateManagement" to true,
            "contextAware" to true,
            "processingState" to processingState.value,
            "visionState" to visionState.value
        )
    }

    /**
     * Retrieves continuous memory.
     */
    override fun getContinuousMemory(): Any? {
        return mapOf(
            "visionHistory" to visionState.value.history,
            "processingHistory" to processingState.value.history,
            "lastAction" to processingState.value.lastAction
        )
    }
}
