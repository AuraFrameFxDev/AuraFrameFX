package dev.aurakai.auraframefx.ai.memory

import dev.aurakai.auraframefx.model.AgentType
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
public data class MemoryItem(
    public val id: String = "mem_${Clock.System.now().toEpochMilliseconds()}",
    public val content: String,
    public val timestamp: Instant = Clock.System.now(),
    public val agent: AgentType,
    public val context: String? = null,
    public val priority: Float = 0.5f,
    public val tags: List<String> = emptyList(),
    public val metadata: Map<String, String> = emptyMap(),
)

@Serializable
public data class MemoryQuery(
    public val query: String,
    public val context: String? = null,
    public val maxResults: Int = 5,
    public val minSimilarity: Float = 0.7f,
    public val tags: List<String> = emptyList(),
    public val timeRange: Pair<Instant, Instant>? = null,
    public val agentFilter: List<AgentType> = emptyList(),
)

@Serializable
public data class MemoryRetrievalResult(
    public val items: List<MemoryItem>,
    public val total: Int,
    public val query: MemoryQuery,
)
