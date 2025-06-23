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
     * Adds an agent to the conference room.
     *
     * The agent becomes an active participant in the multi-agent conversation environment.
     */
    fun join(agent: Agent) {
        agents.add(agent)
    }

    /**
     * Removes the specified agent from the conference room.
     *
     * @param agent The agent to remove from the room.
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
     * @param entry The entry to add to the history.
     */
    fun addToHistory(entry: String) {
        history.add(entry)
    }

    /**
 * Returns the conversation history as a list of entries.
 *
 * @return A list of conversation history entries.
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
 * @return The context as a map of key-value pairs.
 */
fun getContext(): Map<String, Any> = context

    /**
     * Coordinates a multi-agent conversation using the orchestrator based on the provided user input.
     *
     * The orchestrator agent gathers responses from all participating agents using the current shared context and the given user input. All agent responses are added to the conversation history.
     *
     * @param userInput The input message or prompt to initiate the conversation round.
     * @return A list of agent responses generated during this conversation round.
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
     * Aggregates multiple agent response maps into a consensus or unified response map.
     *
     * @param responses A list of maps containing agent responses to be aggregated.
     * @return A map representing the consensus or aggregated responses from all agents.
     */
    fun aggregateConsensus(responses: List<Map<String, dev.aurakai.auraframefx.model.AgentResponse>>): Map<String, dev.aurakai.auraframefx.model.AgentResponse> {
        return orchestrator.aggregateAgentResponses(responses)
    }

    /**
     * Shares the current context with all agents in the room via the orchestrator.
     */
    fun distributeContext() {
        orchestrator.shareContextWithAgents()
    }

    /**
     * Returns a snapshot of the current conference room state, including the room name, agent names, shared context, and conversation history.
     *
     * @return A map containing the room's name, a list of agent names, the shared context, and the conversation history.
     */
    fun getRoomSnapshot(): Map<String, Any> = mapOf(
        "name" to name,
        "agents" to agents.map { it.getName() },
        "context" to context,
        "history" to history
    )

    /**
     * Persists the current conversation history using the provided persistence callback.
     *
     * @param persist A function that handles saving the list of conversation history entries.
     */
    fun persistHistory(persist: (List<String>) -> Unit) {
        persist(history)
    }

    /**
     * Loads conversation history using the provided loader function and replaces the current history.
     *
     * @param load A function that returns a list of conversation history entries to load.
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
     * @param error The error message to log.
     */
    fun logError(error: String) {
        errorLog.add("[${System.currentTimeMillis()}] $error")
    }

    /**
 * Returns the list of error messages logged in the conference room.
 *
 * @return A list of error messages with timestamps.
 */
fun getErrorLog(): List<String> = errorLog

    /**
     * Increments the request count and updates the last activity timestamp for the conference room.
     */
    fun incrementRequestCount() {
        requestCount++
        lastActivityAt = System.currentTimeMillis()
    }

    /**
 * Returns the total number of requests processed in the conference room.
 *
 * @return The current request count.
 */
fun getRequestCount(): Int = requestCount

    /**
     * Adds an asynchronous task to the queue for later execution.
     *
     * @param taskId A unique identifier for the task.
     * @param task The suspend function representing the asynchronous task to be queued.
     */
    fun queueAsyncTask(taskId: String, task: suspend () -> Any) {
        asyncTaskQueue.add(taskId to task)
    }

    /**
     * Executes the next asynchronous task in the queue and notifies registered webhooks of completion or failure.
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
     * Returns operational metadata about the conference room, including name, timestamps, agent count, request count, async queue size, and error count.
     *
     * @return A map containing metadata such as room name, creation and last activity times, agent count, request count, async task queue size, and error count.
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
     * Stores an arbitrary key-value pair in the room's custom properties for extensibility.
     *
     * @param key The name of the custom property.
     * @param value The value to associate with the property.
     */
    fun setCustomProperty(key: String, value: Any) {
        customProperties[key] = value
    }

    /**
 * Retrieves the value of a custom property by its key.
 *
 * @param key The key of the custom property to retrieve.
 * @return The value associated with the key, or null if not set.
 */
fun getCustomProperty(key: String): Any? = customProperties[key]
    /**
 * Returns a copy of all custom key-value properties set for the conference room.
 *
 * @return A map containing all custom properties.
 */
fun getAllCustomProperties(): Map<String, Any> = customProperties.toMap()
}
