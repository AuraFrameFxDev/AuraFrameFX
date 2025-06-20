package dev.aurakai.auraframefx.ai.context

import dev.aurakai.auraframefx.ai.memory.MemoryItem
import dev.aurakai.auraframefx.model.AgentType
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
public data class ContextChain(
    public val id: String = "ctx_${Clock.System.now().toEpochMilliseconds()}",
    public val rootContext: String,
    public val currentContext: String,
    public val contextHistory: List<ContextNode> = emptyList(),
    public val relatedMemories: List<MemoryItem> = emptyList(),
    public val metadata: Map<String, String> = emptyMap(),
    public val priority: Float = 0.5f,
    public val relevanceScore: Float = 0.0f,
    public val lastUpdated: Instant = Clock.System.now(),
    public val agentContext: Map<AgentType, String> = emptyMap(),
)

@Serializable
public data class ContextNode(
    public val id: String,
    public val content: String,
    public val timestamp: Instant = Clock.System.now(),
    public val agent: AgentType,
    public val metadata: Map<String, String> = emptyMap(),
    public val relevance: Float = 0.0f,
    public val confidence: Float = 0.0f,
)

@Serializable
public data class ContextQuery(
    public val query: String,
    public val context: String? = null,
    public val maxChainLength: Int = 10,
    public val minRelevance: Float = 0.6f,
    public val agentFilter: List<AgentType> = emptyList(),
    public val timeRange: Pair<Instant, Instant>? = null,
    public val includeMemories: Boolean = true,
)

@Serializable
public data class ContextChainResult(
    public val chain: ContextChain,
    public val relatedChains: List<ContextChain>,
    public val query: ContextQuery,
)
