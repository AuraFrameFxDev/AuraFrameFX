package dev.aurakai.auraframefx.ai.conference

import dev.aurakai.auraframefx.ai.agents.Agent
import dev.aurakai.auraframefx.ai.agents.GenesisAgent

public class ConferenceRoom(
    public val name: String,
    public val orchestrator: GenesisAgent,
) {
    private val agents = mutableSetOf<Agent>()
    private val history = mutableListOf<String>()
    private var context: Map<String, Any> = emptyMap()

    // --- Advanced Features ---
    public var createdAt: Long = System.currentTimeMillis()
        private set
    public var lastActivityAt: Long = createdAt
        private set
    private val asyncTaskQueue = mutableListOf<Pair<String, suspend () -> Any>>()
    private val webhookCallbacks = mutableListOf<(String, Any) -> Unit>()
    private var requestCount: Int = 0
    private val errorLog = mutableListOf<String>()

    public fun join(agent: Agent) {
        agents.add(agent)
    }

    public fun leave(agent: Agent) {
        agents.remove(agent)
    }

    public fun broadcastContext(newContext: Map<String, Any>) {
        context = newContext
        // Optionally, notify all agents
    }

    public fun addToHistory(entry: String) {
        history.add(entry)
    }

    public fun getHistory(): List<String> = history
    public fun getAgents(): Set<Agent> = agents
    public fun getContext(): Map<String, Any> = context

    suspend fun orchestrateConversation(userInput: String): List<Any> {
        // Use GenesisAgent to orchestrate a multi-agent conversation
        public val agentList = agents.toList()
        public val responses = orchestrator.participateWithAgents(context, agentList, userInput)
        // Add to history
        responses.values.forEach { addToHistory(it.toString()) }
        return responses.values.toList()
    }

    /**
     * Aggregates responses from all agents for consensus or decision-making.
     * Returns the consensus response or a map of agent responses.
     */
    public fun aggregateConsensus(responses: List<Map<String, dev.aurakai.auraframefx.model.AgentResponse>>): Map<String, dev.aurakai.auraframefx.model.AgentResponse> {
        return orchestrator.aggregateAgentResponses(responses)
    }

    /**
     * Distributes the current context/memory to all agents for shared state.
     */
    public fun distributeContext() {
        orchestrator.shareContextWithAgents()
    }

    /**
     * Returns a snapshot of the conference room state for diagnostics or UI.
     */
    public fun getRoomSnapshot(): Map<String, Any> = mapOf(
        "name" to name,
        "agents" to agents.map { it.getName() },
        "context" to context,
        "history" to history
    )

    public fun persistHistory(persist: (List<String>) -> Unit) {
        persist(history)
    }

    public fun loadHistory(load: () -> List<String>) {
        history.clear()
        history.addAll(load())
    }

    public fun registerWebhook(callback: (event: String, payload: Any) -> Unit) {
        webhookCallbacks.add(callback)
    }

    public fun logError(error: String) {
        errorLog.add("[${System.currentTimeMillis()}] $error")
    }

    public fun getErrorLog(): List<String> = errorLog

    public fun incrementRequestCount() {
        requestCount++
        lastActivityAt = System.currentTimeMillis()
    }

    public fun getRequestCount(): Int = requestCount

    public fun queueAsyncTask(taskId: String, task: suspend () -> Any) {
        asyncTaskQueue.add(taskId to task)
    }

    suspend fun processNextAsyncTask(): Any? {
        public val next = asyncTaskQueue.firstOrNull() ?: return null
        asyncTaskQueue.removeAt(0)
        return try {
            public val result = next.second()
            webhookCallbacks.forEach { it("task_completed", result) }
            result
        } catch (e: Exception) {
            logError("Async task failed: ${e.message}")
            webhookCallbacks.forEach { it("task_failed", e.message ?: "Unknown error") }
            null
        }
    }

    // --- Utility & Diagnostics ---
    public fun getRoomMetadata(): Map<String, Any> = mapOf(
        "name" to name,
        "createdAt" to createdAt,
        "lastActivityAt" to lastActivityAt,
        "agentCount" to agents.size,
        "requestCount" to requestCount,
        "asyncQueueSize" to asyncTaskQueue.size,
        "errorCount" to errorLog.size
    )

    public fun clearErrorLog() {
        errorLog.clear()
    }

    public fun clearHistory() {
        history.clear()
    }

    public fun clearAsyncQueue() {
        asyncTaskQueue.clear()
    }

    // --- Extensibility: Custom Room Properties ---
    private val customProperties = mutableMapOf<String, Any>()
    public fun setCustomProperty(key: String, value: Any) {
        customProperties[key] = value
    }

    public fun getCustomProperty(key: String): Any? = customProperties[key]
    public fun getAllCustomProperties(): Map<String, Any> = customProperties.toMap()
}
