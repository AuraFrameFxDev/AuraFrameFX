package dev.aurakai.auraframefx.ai.conference

import dev.aurakai.auraframefx.ai.agents.Agent
import dev.aurakai.auraframefx.ai.agents.GenesisAgent

class ConferenceRoom(
    val name: String,
    val orchestrator: GenesisAgent,
) {
    private val agents = mutableSetOf<Agent>()
    private val history = mutableListOf<String>()
    private var context: Map<String, Any> = emptyMap()

    // --- Advanced Features ---
    var createdAt: Long = System.currentTimeMillis()
        private set
    var lastActivityAt: Long = createdAt
        private set
    private val asyncTaskQueue = mutableListOf<Pair<String, suspend () -> Any>>()
    private val webhookCallbacks = mutableListOf<(String, Any) -> Unit>()
    private var requestCount: Int = 0
    private val errorLog = mutableListOf<String>()

    /**
     * Adds an agent to the conference room's set of participants.
     *
     * If the agent is already present, no action is taken.
     */
    fun join(agent: Agent) {
        agents.add(agent)
    }

    /**
     * Removes the specified agent from the conference room.
     *
     * If the agent is not present, no action is taken.
     */
    fun leave(agent: Agent) {
        agents.remove(agent)
    }

    /**
     * Replaces the shared context for the conference room with the provided map.
     *
     * This updates the context accessible to all agents in the room.
     *
     * @param newContext The new context map to broadcast to all agents.
     */
    fun broadcastContext(newContext: Map<String, Any>) {
        context = newContext
        // Optionally, notify all agents
    }

    /**
     * Appends an entry to the conversation history.
     *
     * @param entry The text to add to the history log.
     */
    fun addToHistory(entry: String) {
        history.add(entry)
    }

    /**
 * Returns the conversation history as a list of entries.
 *
 * @return A list of conversation history entries in chronological order.
 */
fun getHistory(): List<String> = history
    /**
 * Returns the set of agents currently participating in the conference room.
 *
 * @return A set containing all active agents in the room.
 */
fun getAgents(): Set<Agent> = agents
    /**
 * Returns the current shared context map for the conference room.
 *
 * The context contains key-value pairs representing shared state or information accessible to all agents in the room.
 *
 * @return The current context as a map.
 */
fun getContext(): Map<String, Any> = context

    /**
     * Coordinates a multi-agent conversation using the orchestrator based on user input.
     *
     * The orchestrator agent gathers responses from all participating agents using the current shared context and the provided user input. All agent responses are appended to the conversation history and returned as a list.
     *
     * @param userInput The input message or prompt to initiate the conversation round.
     * @return A list of agent responses generated during the conversation.
     */
    suspend fun orchestrateConversation(userInput: String): List<Any> {
        // Use GenesisAgent to orchestrate a multi-agent conversation
        val agentList = agents.toList()
        val responses = orchestrator.participateWithAgents(context, agentList, userInput)
        // Add to history
        responses.values.forEach { addToHistory(it.toString()) }
        return responses.values.toList()
    }

    /**
     * Aggregates multiple agent response maps into a consensus or combined response map.
     *
     * @param responses A list of maps containing agent responses to be aggregated.
     * @return A map representing the consensus or combined responses as determined by the orchestrator.
     */
    fun aggregateConsensus(responses: List<Map<String, dev.aurakai.auraframefx.model.AgentResponse>>): Map<String, dev.aurakai.auraframefx.model.AgentResponse> {
        return orchestrator.aggregateAgentResponses(responses)
    }

    /**
     * Shares the current context with all agents in the conference room.
     *
     * Instructs the orchestrator to distribute the shared state or memory to every participating agent.
     */
    fun distributeContext() {
        orchestrator.shareContextWithAgents()
    }

    /**
     * Returns a snapshot of the current conference room state, including the room name, agent names, shared context, and conversation history.
     *
     * @return A map containing the room's name, a list of agent names, the current context, and the conversation history.
     */
    fun getRoomSnapshot(): Map<String, Any> = mapOf(
        "name" to name,
        "agents" to agents.map { it.getName() },
        "context" to context,
        "history" to history
    )

    /**
     * Persists the current conversation history using the provided persistence function.
     *
     * @param persist A function that handles saving the list of history entries.
     */
    fun persistHistory(persist: (List<String>) -> Unit) {
        persist(history)
    }

    /**
     * Loads conversation history using the provided loader function and replaces the current history.
     *
     * @param load A function that returns a list of history entries to be loaded.
     */
    fun loadHistory(load: () -> List<String>) {
        history.clear()
        history.addAll(load())
    }

    /**
     * Registers a webhook callback to be invoked on specific room events.
     *
     * The callback receives the event name and an associated payload when triggered during asynchronous task processing or other relevant events.
     */
    fun registerWebhook(callback: (event: String, payload: Any) -> Unit) {
        webhookCallbacks.add(callback)
    }

    /**
     * Appends an error message with a timestamp to the error log.
     *
     * @param error The error message to record.
     */
    fun logError(error: String) {
        errorLog.add("[${System.currentTimeMillis()}] $error")
    }

    /**
 * Returns the current list of error log entries for the conference room.
 *
 * @return A list of error messages with timestamps.
 */
fun getErrorLog(): List<String> = errorLog

    /**
     * Increments the request counter and updates the last activity timestamp for the conference room.
     */
    fun incrementRequestCount() {
        requestCount++
        lastActivityAt = System.currentTimeMillis()
    }

    /**
 * Returns the total number of requests processed by the conference room.
 *
 * @return The current request count.
 */
fun getRequestCount(): Int = requestCount

    /**
     * Adds an asynchronous task to the queue for later processing.
     *
     * @param taskId A unique identifier for the task.
     * @param task The suspendable function representing the asynchronous task to be queued.
     */
    fun queueAsyncTask(taskId: String, task: suspend () -> Any) {
        asyncTaskQueue.add(taskId to task)
    }

    /**
     * Executes the next asynchronous task in the queue and returns its result.
     *
     * If the task completes successfully, registered webhook callbacks are notified with a "task_completed" event and the result.
     * If the task fails, the error is logged, webhook callbacks are notified with a "task_failed" event and the error message, and `null` is returned.
     *
     * @return The result of the executed task, or `null` if the queue is empty or the task fails.
     */
    suspend fun processNextAsyncTask(): Any? {
        val next = asyncTaskQueue.firstOrNull() ?: return null
        asyncTaskQueue.removeAt(0)
        return try {
            val result = next.second()
            webhookCallbacks.forEach { it("task_completed", result) }
            result
        } catch (e: Exception) {
            logError("Async task failed: ${e.message}")
            webhookCallbacks.forEach { it("task_failed", e.message ?: "Unknown error") }
            null
        }
    }

    /**
     * Returns metadata about the conference room, including its name, creation and last activity timestamps, agent count, request count, async task queue size, and error count.
     *
     * @return A map containing room metadata for diagnostics or monitoring purposes.
     */
    fun getRoomMetadata(): Map<String, Any> = mapOf(
        "name" to name,
        "createdAt" to createdAt,
        "lastActivityAt" to lastActivityAt,
        "agentCount" to agents.size,
        "requestCount" to requestCount,
        "asyncQueueSize" to asyncTaskQueue.size,
        "errorCount" to errorLog.size
    )

    /**
     * Removes all entries from the error log.
     */
    fun clearErrorLog() {
        errorLog.clear()
    }

    /**
     * Removes all entries from the conversation history.
     */
    fun clearHistory() {
        history.clear()
    }

    /**
     * Removes all pending asynchronous tasks from the queue.
     */
    fun clearAsyncQueue() {
        asyncTaskQueue.clear()
    }

    // --- Extensibility: Custom Room Properties ---
    private val customProperties = mutableMapOf<String, Any>()
    /**
     * Sets a custom property for the conference room.
     *
     * Associates the specified value with the given key in the room's custom properties map, allowing for extensibility and storage of additional metadata.
     */
    fun setCustomProperty(key: String, value: Any) {
        customProperties[key] = value
    }

    /**
 * Retrieves the value of a custom property by its key.
 *
 * @param key The key identifying the custom property.
 * @return The value associated with the key, or null if not set.
 */
fun getCustomProperty(key: String): Any? = customProperties[key]
    /**
 * Returns a copy of all custom properties set for the conference room.
 *
 * @return A map containing all custom property key-value pairs.
 */
fun getAllCustomProperties(): Map<String, Any> = customProperties.toMap()
}
