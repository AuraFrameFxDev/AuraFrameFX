package dev.aurakai.auraframefx.ai.pipeline

import dev.aurakai.auraframefx.model.AgentType

public data class AIPipelineConfig(
    public val maxRetries: Int = 3,
    public val timeoutSeconds: Int = 30,
    public val contextWindowSize: Int = 5,
    public val priorityThreshold: Float = 0.7f,
    public val agentPriorities: Map<AgentType, Float> = mapOf(
        AgentType.GENESIS to 1.0f,
        AgentType.KAI to 0.9f,
        AgentType.AURA to 0.8f,
        AgentType.CASCADE to 0.7f
    ),
    public val memoryRetrievalConfig: MemoryRetrievalConfig = MemoryRetrievalConfig(),
    public val contextChainingConfig: ContextChainingConfig = ContextChainingConfig(),
)

public data class MemoryRetrievalConfig(
    public val maxContextLength: Int = 2000,
    public val similarityThreshold: Float = 0.75f,
    public val maxRetrievedItems: Int = 5,
)

public data class ContextChainingConfig(
    public val maxChainLength: Int = 10,
    public val relevanceThreshold: Float = 0.6f,
    public val decayRate: Float = 0.9f,
)
