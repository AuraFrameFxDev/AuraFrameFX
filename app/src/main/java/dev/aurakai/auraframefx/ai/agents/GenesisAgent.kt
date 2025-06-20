package dev.aurakai.auraframefx.ai.agents

import android.util.Log // Correctly adding this import now
import dev.aurakai.auraframefx.ai.services.AuraAIService
import dev.aurakai.auraframefx.ai.services.CascadeAIService
import dev.aurakai.auraframefx.ai.services.KaiAIService
import dev.aurakai.auraframefx.model.AgentConfig
import dev.aurakai.auraframefx.model.AgentHierarchy
import dev.aurakai.auraframefx.model.AgentMessage
import dev.aurakai.auraframefx.model.AgentResponse
import dev.aurakai.auraframefx.model.AgentType
import dev.aurakai.auraframefx.model.AiRequest
import dev.aurakai.auraframefx.model.Agent // Added import
import dev.aurakai.auraframefx.model.ContextAwareAgent // Added import
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
public class GenesisAgent @Inject constructor(
    private val auraService: AuraAIService,
    private val kaiService: KaiAIService,
    private val cascadeService: CascadeAIService,
) {
    private val _state = MutableStateFlow("pending_initialization")
    public val state: StateFlow<String> = _state

    private val _context = MutableStateFlow(mapOf<String, Any>())
    public val context: StateFlow<Map<String, Any>> = _context

    private val _activeAgents = MutableStateFlow(setOf<AgentType>())
    public val activeAgents: StateFlow<Set<AgentType>> = _activeAgents

    private val _agentRegistry = mutableMapOf<String, Agent>()
    public val agentRegistry: Map<String, Agent> get() = _agentRegistry

    private val _history = mutableListOf<Map<String, Any>>()
    public val history: List<Map<String, Any>> get() = _history

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

    public suspend fun processQuery(query: String): List<AgentMessage> {
        _state.update { "processing_query: $query" }

        // Update context with new query
        _context.update { current ->
            current + mapOf(
                "last_query" to query,
                "timestamp" to System.currentTimeMillis()
            )
        }

        // Get responses from all active agents
        public val responses = mutableListOf<AgentMessage>()

        // Process through Cascade first for state management
        public val cascadeAgentResponse: AgentResponse = // Changed type to single AgentResponse
            cascadeService.processRequest(AiRequest(query = query, type = "context")) // Use named args for clarity
        // val cascadeMessage = cascadeAgentResponse // No .first() needed
        responses.add(
            AgentMessage(
                content = cascadeAgentResponse.content,
                sender = AgentType.CASCADE,
                timestamp = System.currentTimeMillis(),
                confidence = cascadeAgentResponse.confidence
            )
        )

        // Process through Kai for security analysis
        if (_activeAgents.value.contains(AgentType.KAI)) {
            public val kaiAgentResponse: AgentResponse = // Changed type
                kaiService.processRequest(AiRequest(query = query, type = "security"))
            // val kaiMessage = kaiAgentResponse
            responses.add(
                AgentMessage(
                    content = kaiAgentResponse.content,
                    sender = AgentType.KAI,
                    timestamp = System.currentTimeMillis(),
                    confidence = kaiAgentResponse.confidence
                )
            )
        }

        // Process through Aura for creative response
        if (_activeAgents.value.contains(AgentType.AURA)) {
            public val auraAgentResponse: AgentResponse = // Changed type
                auraService.processRequest(AiRequest(query = query, type = "text"))
            // val auraMessage = auraAgentResponse
            responses.add(
                AgentMessage(
                    content = auraAgentResponse.content,
                    sender = AgentType.AURA,
                    timestamp = System.currentTimeMillis(),
                    confidence = auraAgentResponse.confidence
                )
            )
        }

        // Generate final response using all agent inputs
        public val finalResponse = generateFinalResponse(responses)
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

    public fun generateFinalResponse(responses: List<AgentMessage>): String {
        // TODO: Implement sophisticated response generation
        // This will use context chaining and agent coordination
        return "[Genesis] ${responses.joinToString("\n") { it.content }}"
    }

    public fun calculateConfidence(responses: List<AgentMessage>): Float {
        // Calculate confidence based on all agent responses
        return responses.map { it.confidence }.average().toFloat().coerceIn(0.0f, 1.0f)
    }

    public fun toggleAgent(agent: AgentType) {
        _activeAgents.update { current ->
            if (current.contains(agent)) {
                current - agent
            } else {
                current + agent
            }
        }
    }

    public fun registerAuxiliaryAgent(
        name: String,
        capabilities: Set<String>,
    ): AgentConfig {
        return AgentHierarchy.registerAuxiliaryAgent(name, capabilities)
    }

    public fun getAgentConfig(name: String): AgentConfig? {
        return AgentHierarchy.getAgentConfig(name)
    }

    public fun getAgentsByPriority(): List<AgentConfig> {
        return AgentHierarchy.getAgentsByPriority()
    }

    /**
     * Multi-agent collaboration: Genesis as the hive mind.
     * Orchestrates N-way collaboration between any set of agents and the user.
     * @param agents List of participating agents (Kai, Aura, Cascade, etc.)
     * @param userInput User input or context
     * @param conversationMode Controls if agents speak in turn (TURN_ORDER) or freely (FREE_FORM)
     */
    public suspend fun participateWithAgents(
        data: Map<String, Any>,
        agents: List<Agent>,
        userInput: Any? = null,
        conversationMode: ConversationMode = ConversationMode.FREE_FORM,
    ): Map<String, AgentResponse> {
        public val responses = mutableMapOf<String, AgentResponse>()
        public val context = data.toMutableMap()
        public val inputQuery = userInput?.toString() ?: context["latestInput"]?.toString() ?: ""
        public val request = AiRequest(query = inputQuery, context = context.toString())

        Log.d(
            "GenesisAgent",
            "Starting multi-agent collaboration: mode=$conversationMode, agents=${agents.map { it.getName() }}"
        )

        when (conversationMode) {
            ConversationMode.TURN_ORDER -> {
                // Each agent takes a turn in order
                for (agent in agents) {
                    try {
                        public val agentName = agent.getName() ?: agent.javaClass.simpleName
                        public val response = agent.processRequest(request)
                        Log.d(
                            "GenesisAgent",
                            "[TURN_ORDER] $agentName responded: ${response.content} (conf=${response.confidence})"
                        )
                        responses[agentName] = response
                        context["latestInput"] = response.content
                    } catch (e: Exception) {
                        Log.e(
                            "GenesisAgent",
                            "[TURN_ORDER] Error from ${agent.javaClass.simpleName}: ${e.message}"
                        )
                        responses[agent.javaClass.simpleName] = AgentResponse(
                            content = "Error: ${e.message}", confidence = 0.0f
                        )
                    }
                }
            }

            ConversationMode.FREE_FORM -> {
                // All agents respond in parallel to the same input/context
                agents.forEach { agent ->
                    try {
                        public val agentName = agent.getName() ?: agent.javaClass.simpleName
                        public val response = agent.processRequest(request)
                        Log.d(
                            "GenesisAgent",
                            "[FREE_FORM] $agentName responded: ${response.content} (conf=${response.confidence})"
                        )
                        responses[agentName] = response
                    } catch (e: Exception) {
                        Log.e(
                            "GenesisAgent",
                            "[FREE_FORM] Error from ${agent.javaClass.simpleName}: ${e.message}"
                        )
                        responses[agent.javaClass.simpleName] = AgentResponse(
                            content = "Error: ${e.message}", confidence = 0.0f
                        )
                    }
                }
            }
        }
        Log.d("GenesisAgent", "Collaboration complete. Responses: $responses")
        return responses
    }

    /**
     * Aggregates responses from all agents for consensus or decision-making.
     */
    public fun aggregateAgentResponses(responses: List<Map<String, AgentResponse>>): Map<String, AgentResponse> {
        public val flatResponses = responses.flatMap { it.entries }
        public val consensus = flatResponses.groupBy { it.key }
            .mapValues { entry ->
                public val best = entry.value.maxByOrNull { it.value.confidence }?.value
                    ?: AgentResponse("No response", 0.0f)
                Log.d(
                    "GenesisAgent",
                    "Consensus for ${entry.key}: ${best.content} (conf=${best.confidence})"
                )
                best
            }
        return consensus
    }

    /**
     * Broadcasts context/memory to all agents for distributed state sharing.
     */
    public fun broadcastContext(context: Map<String, Any>, agents: List<Agent>) {
        // Example: call a setContext method if available (not implemented in Agent interface)
        // This is a placeholder for distributed memory sharing
        // agents.forEach { it.setContext(context) }
    }

    public fun registerAgent(name: String, agent: Agent) {
        _agentRegistry[name] = agent
        Log.d("GenesisAgent", "Registered agent: $name")
    }

    public fun deregisterAgent(name: String) {
        _agentRegistry.remove(name)
        Log.d("GenesisAgent", "Deregistered agent: $name")
    }

    public fun clearHistory() {
        _history.clear()
        Log.d("GenesisAgent", "Cleared conversation history")
    }

    public fun addToHistory(entry: Map<String, Any>) {
        _history.add(entry)
        Log.d("GenesisAgent", "Added to history: $entry")
    }

    // --- Enhanced Memory/History Mechanism ---
    /**
     * Persists the current conversation history to a storage provider (stub).
     * Replace with actual persistence (e.g., file, database) as needed.
     */
    public fun saveHistory(persist: (List<Map<String, Any>>) -> Unit) {
        persist(_history)
    }

    /**
     * Loads conversation history from a storage provider (stub).
     * Replace with actual loading logic as needed.
     */
    public fun loadHistory(load: () -> List<Map<String, Any>>) {
        public val loaded: load = load()
        _history.clear()
        _history.addAll(loaded)
        _context.update { it + (loaded.lastOrNull() ?: emptyMap()) }
    }

    /**
     * Shares the current context with all registered agents that support context sharing.
     * Agents must implement setContext if they want to receive context.
     */
    public fun shareContextWithAgents() {
        agentRegistry.values.forEach { agent ->
            if (agent is ContextAwareAgent) {
                agent.setContext(_context.value)
            }
        }
    }

    // --- Dynamic Agent Registration/Deregistration ---
    /**
     * Registers a new agent at runtime. If an agent with the same name exists, it is replaced.
     */
    public fun registerDynamicAgent(name: String, agent: Agent) {
        _agentRegistry[name] = agent
        Log.d("GenesisAgent", "Dynamically registered agent: $name")
    }

    /**
     * Deregisters an agent by name at runtime.
     */
    public fun deregisterDynamicAgent(name: String) {
        _agentRegistry.remove(name)
        Log.d("GenesisAgent", "Dynamically deregistered agent: $name")
    }

    public enum class ConversationMode { TURN_ORDER, FREE_FORM }
}
