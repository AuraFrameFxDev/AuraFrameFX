package dev.aurakai.auraframefx.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.model.AgentType
import javax.inject.Inject

/**
 * ViewModel for managing Genesis Agent operations and state 
 */
@HiltViewModel
class GenesisAgentViewModel @Inject constructor() : ViewModel() {
    
    /**
     * Returns a list of available agents sorted by priority
     */
    fun getAgentsByPriority(): List<AgentType> {
        return listOf(
            AgentType.CASCADE,
            AgentType.AURA,
            AgentType.KAI,
            AgentType.NEURAL_WHISPER,
            AgentType.AURA_SHIELD,
            AgentType.GENKIT_MASTER
        )
    }
    
    /**
     * Returns active task assignments by agent
     */
    fun getTaskAssignments(): Map<AgentType, List<String>> {
        // This would normally be loaded from a repository or service
        return mapOf(
            AgentType.CASCADE to listOf("Task coordination", "System analysis"),
            AgentType.AURA to listOf("Creative processing", "UI enhancement"),
            AgentType.KAI to listOf("Security verification", "Input validation"),
            AgentType.NEURAL_WHISPER to listOf("Context processing")
        )
    }
}
