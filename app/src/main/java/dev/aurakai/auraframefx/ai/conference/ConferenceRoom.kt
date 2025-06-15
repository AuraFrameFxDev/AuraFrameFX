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
     * Adds an agent to the conference room if not already present.
     *
     * If the agent is already a participant, this method does nothing.
     */
    fun join(agent: Agent) {
        agents.add(agent)
    }

    /**
     * Removes an agent from the conference room if present.
     *
     * If the agent is not currently a participant, this method has no effect.
     */
    fun leave(agent: Agent) {
        agents.remove(agent)
    }

    /**
     * Updates the shared context for the conference room with the provided map.
     *
     * Replaces the current context accessible to all agents with the new context.
     *
     * @param newContext The new shared context to set for the room.
     */
    fun broadcastContext(newContext: Map<String, Any>) {
        context = newContext
        // Optionally, notify all agents
    }

    /**
     * Appends a text entry to the conversation history.
     *
     * @param entry The conversation message or event to record.
     */
    fun addToHistory(entry: String) {
        history.add(entry)
    }

    /**
 * Retrieves the conversation history as a list of entries in chronological order.
 *
 * @return The list of conversation history entries.
 */
fun getHistory(): List<String> = history
    /**
 * Retrieves the set of agents currently participating in the conference room.
 *
 * @return A set of active agents.
 */
fun getAgents(): Set<Agent> = agents
    /**
 * Retrieves the current shared context for the conference room.
 *
 * The context is a map of key-value pairs representing shared state accessible to all agents.
 *
 * @return The current shared context map.
 */
fun getContext(): Map<String, Any> = context

    /**
     * Orchestrates a conversation round among all agents using the orchestrator and user input.
     *
     * The orchestrator coordinates participating agents to generate responses based on the current shared context and the provided user input. All responses are added to the conversation history and returned as a list.
     *
     * @param userInput The message or prompt to initiate the conversation.
     * @return A list of responses from all participating agents.
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
     * Aggregates multiple agent response maps into a single consensus map using the orchestrator.
     *
     * @param responses A list of agent response maps to be combined.
     * @return A map representing the consensus responses as determined by the orchestrator.
     */
    fun aggregateConsensus(responses: List<Map<String, dev.aurakai.auraframefx.model.AgentResponse>>): Map<String, dev.aurakai.auraframefx.model.AgentResponse> {
        return orchestrator.aggregateAgentResponses(responses)
    }

    /**
     * Instructs the orchestrator to distribute the current shared context to all participating agents.
     */
    fun distributeContext() {
        orchestrator.shareContextWithAgents()
    }

    /**
     * Returns a map representing the current state of the conference room.
     *
     * The snapshot includes the room's name, a list of agent names, the shared context, and the conversation history.
     *
     * @return A map with keys "name", "agents", "context", and "history" reflecting the room's current state.
     */
    fun getRoomSnapshot(): Map<String, Any> = mapOf(
        "name" to name,
        "agents" to agents.map { it.getName() },
        "context" to context,
        "history" to history
    )

    /**
     * Persists the current conversation history using a provided function.
     *
     * @param persist Function to handle saving the list of conversation history entries.
     */
    fun persistHistory(persist: (List<String>) -> Unit) {
        persist(history)
    }

    /**
     * Replaces the current conversation history with entries loaded from the provided function.
     *
     * @param load Function that returns a list of history entries to populate the history.
     */
    fun loadHistory(load: () -> List<String>) {
        history.clear()
        history.addAll(load())
    }

    /**
     * Registers a callback to be invoked on specific conference room events.
     *
     * The callback is triggered with the event name and a payload, typically during asynchronous task completion or failure.
     */
    fun registerWebhook(callback: (event: String, payload: Any) -> Unit) {
        webhookCallbacks.add(callback)
    }

    /**
     * Records an error message with a timestamp in the error log.
     *
     * @param error The error message to log.
     */
    fun logError(error: String) {
        errorLog.add("[${System.currentTimeMillis()}] $error")
    }

    /**
 * Retrieves the list of error log entries for the conference room.
 *
 * @return A list of timestamped error messages.
 */
fun getErrorLog(): List<String> = errorLog

    /**
     * Increments the number of processed requests and updates the last activity timestamp.
     */
    fun incrementRequestCount() {
        requestCount++
        lastActivityAt = System.currentTimeMillis()
    }

    /**
 * Returns the number of requests that have been processed in this conference room.
 *
 * @return The total processed request count.
 */
fun getRequestCount(): Int = requestCount

    /**
     * Queues an asynchronous task with a unique identifier for sequential processing.
     *
     * @param taskId Unique identifier for the task.
     * @param task Suspendable function representing the asynchronous task to be added to the queue.
     */
    fun queueAsyncTask(taskId: String, task: suspend () -> Any) {
        asyncTaskQueue.add(taskId to task)
    }

    /**
     * Executes and removes the next asynchronous task in the queue.
     *
     * Notifies registered webhook callbacks with a "task_completed" event and the result on success, or a "task_failed" event and the error message on failure. Returns `null` if the queue is empty or the task fails.
     *
     * @return The result of the executed task, or `null` if no task is available or execution fails.
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
     * Retrieves diagnostic metadata about the conference room.
     *
     * @return A map containing the room's name, creation and last activity timestamps, agent count, request count, asynchronous task queue size, and error count.
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
     * Clears all entries from the error log.
     */
    fun clearErrorLog() {
        errorLog.clear()
    }

    /**
     * Clears all conversation history entries from the room.
     */
    fun clearHistory() {
        history.clear()
    }

    /**
     * Clears all pending asynchronous tasks from the queue.
     */
    fun clearAsyncQueue() {
        asyncTaskQueue.clear()
    }

    // --- Extensibility: Custom Room Properties ---
    private val customProperties = mutableMapOf<String, Any>()
    /**
     * Sets a custom key-value property for the conference room.
     *
     * Stores additional metadata or extensible information associated with the specified key.
     */
    fun setCustomProperty(key: String, value: Any) {
        customProperties[key] = value
    }

    /**
 * Returns the value of a custom property for the given key, or null if the key is not present.
 *
 * @param key The custom property key to look up.
 * @return The associated value, or null if the property is not set.
 */
fun getCustomProperty(key: String): Any? = customProperties[key]
    /**
 * Retrieves a copy of all custom properties associated with the conference room.
 *
 * @return A map containing all custom property key-value pairs.
 */
fun getAllCustomProperties(): Map<String, Any> = customProperties.toMap()
}
