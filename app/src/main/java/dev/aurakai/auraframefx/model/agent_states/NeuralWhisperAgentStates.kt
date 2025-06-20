package dev.aurakai.auraframefx.model.agent_states

// TODO: Define actual properties for these states/events.
// TODO: Classes reported as unused or need implementation. Ensure these are utilized by NeuralWhisperAgent.

public data class ActiveContext(
    // Renamed from ActiveContexts (singular)
    public val contextId: String,
    public val description: String? = null,
    public val relatedData: Map<String, String> = emptyMap(),
    // Add other relevant active context properties
)

// ContextChain could be a list of context snapshots or events
public data class ContextChainEvent(
    public val eventId: String,
    public val timestamp: Long = System.currentTimeMillis(),
    public val contextSnapshot: String? = null, // e.g., JSON representation of a context state
    // Add other relevant chain event properties
)

public data class LearningEvent(
    public val eventId: String,
    public val description: String,
    public val outcome: String? = null, // e.g., "positive_reinforcement", "correction"
    public val dataLearned: Map<String, String> = emptyMap(),
    // Add other relevant learning event properties
)
