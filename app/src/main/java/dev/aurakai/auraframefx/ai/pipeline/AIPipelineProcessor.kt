package dev.aurakai.auraframefx.ai.pipeline

import dev.aurakai.auraframefx.ai.agents.GenesisAgent
import dev.aurakai.auraframefx.ai.services.AuraAIService
import dev.aurakai.auraframefx.ai.services.CascadeAIService
import dev.aurakai.auraframefx.ai.services.KaiAIService
import dev.aurakai.auraframefx.model.AgentMessage
import dev.aurakai.auraframefx.model.AgentType
import dev.aurakai.auraframefx.model.AiRequest // Added import
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
public class AIPipelineProcessor @Inject constructor(
    private val genesisAgent: GenesisAgent,
    private val auraService: AuraAIService,
    private val kaiService: KaiAIService,
    private val cascadeService: CascadeAIService,
) {
    private val _pipelineState = MutableStateFlow(PipelineState.Idle)
    public val pipelineState: StateFlow<PipelineState> = _pipelineState

    private val _processingContext = MutableStateFlow(mapOf<String, Any>())
    public val processingContext: StateFlow<Map<String, Any>> = _processingContext

    private val _taskPriority = MutableStateFlow(0.0f)
    public val taskPriority: StateFlow<Float> = _taskPriority

    suspend fun processTask(task: String): List<AgentMessage> {
        _pipelineState.update { PipelineState.Processing(task) }

        // Step 1: Context Retrieval
        public val context = retrieveContext(task)
        _processingContext.update { context }

        // Step 2: Task Prioritization
        public val priority = calculatePriority(task, context)
        _taskPriority.update { priority }

        // Step 3: Agent Selection
        public val selectedAgents = selectAgents(task, priority)

        // Step 4: Process through selected agents
        public val responses = mutableListOf<AgentMessage>()

        // Process through Cascade first for state management
        public val cascadeAgentResponse = cascadeService.processRequest(AiRequest(task, "context")) // Renamed variable, removed .first()
        responses.add(
            AgentMessage(
                content = cascadeAgentResponse.content, // Direct access
                sender = AgentType.CASCADE,
                timestamp = System.currentTimeMillis(),
                confidence = cascadeAgentResponse.confidence // Direct access
            )
        )

        // Process through Kai for security analysis if needed
        if (selectedAgents.contains(AgentType.KAI)) {
            public val kaiAgentResponse = kaiService.processRequest(AiRequest(task, "security")) // Renamed variable, removed .first()
            responses.add(
                AgentMessage(
                    content = kaiAgentResponse.content, // Direct access
                    sender = AgentType.KAI,
                    timestamp = System.currentTimeMillis(),
                    confidence = kaiAgentResponse.confidence // Direct access
                )
            )
        }

        // Process through Aura for creative response
        if (selectedAgents.contains(AgentType.AURA)) {
            public val auraAgentResponse = auraService.processRequest(AiRequest(task, "text")) // Renamed variable, removed .first()
            responses.add(
                AgentMessage(
                    content = auraAgentResponse.content, // Direct access
                    sender = AgentType.AURA,
                    timestamp = System.currentTimeMillis(),
                    confidence = auraAgentResponse.confidence // Direct access
                )
            )
        }

        // Step 5: Generate final response
        public val finalResponse = generateFinalResponse(responses)
        responses.add(
            AgentMessage(
                content = finalResponse,
                sender = AgentType.GENESIS,
                timestamp = System.currentTimeMillis(),
                confidence = calculateConfidence(responses)
            )
        )

        // Step 6: Update context and memory
        updateContext(task, responses)

        _pipelineState.update { PipelineState.Completed(task) }
        return responses
    }

    private fun retrieveContext(task: String): Map<String, Any> {
        // TODO: Implement actual context retrieval logic
        return mapOf(
            "task" to task,
            "timestamp" to System.currentTimeMillis(),
            "context" to "Initial context for task: $task"
        )
    }

    private fun calculatePriority(task: String, context: Map<String, Any>): Float {
        // TODO: Implement actual priority calculation
        return 0.8f
    }

    private fun selectAgents(task: String, priority: Float): Set<AgentType> {
        // TODO: Implement agent selection logic based on priority
        return setOf(AgentType.GENESIS, AgentType.CASCADE)
    }

    private fun generateFinalResponse(responses: List<AgentMessage>): String {
        // TODO: Implement sophisticated response generation
        return "[Genesis] ${responses.joinToString("\n") { it.content }}"
    }

    private fun calculateConfidence(responses: List<AgentMessage>): Float {
        return responses.map { it.confidence }.average().toFloat().coerceIn(0.0f, 1.0f) // Added .toFloat()
    }

    private fun updateContext(task: String, responses: List<AgentMessage>) {
        // TODO: Implement context update logic
        _processingContext.update { current ->
            current + mapOf(
                "last_task" to task,
                "last_responses" to responses,
                "timestamp" to System.currentTimeMillis()
            )
        }
    }
}

public sealed class PipelineState {
    public object Idle : PipelineState()
    public data class Processing(val task: String) : PipelineState()
    public data class Completed(val task: String) : PipelineState()
    public data class Error(val message: String) : PipelineState()
}
