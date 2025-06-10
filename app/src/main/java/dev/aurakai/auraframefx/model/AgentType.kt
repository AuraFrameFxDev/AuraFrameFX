package dev.aurakai.auraframefx.model

/**
 * Enum representing different types of AI agents in the system.
 * TODO: Reported as unused symbol. Ensure this enum is used.
 */
enum class AgentType {
    /**
     * Genesis Agent - Core orchestrator or foundational AI.
     * TODO: Reported as unused symbol.
     */
    GENESIS,

    /**
     * Kai Agent - Specialized AI, possibly for UI interaction or specific tasks.
     * TODO: Reported as unused symbol.
     */
    KAI,

    /**
     * Aura Agent - General purpose assistant AI.
     * TODO: Reported as unused symbol.
     */
    AURA,

    /**
     * Cascade Agent - AI for stateful processing, vision, etc.
     * TODO: Reported as unused symbol.
     */
    CASCADE,

    /**
     * NeuralWhisper Agent - AI for context chaining, learning, audio processing.
     * TODO: Reported as unused symbol.
     */
    NEURAL_WHISPER,

    /**
     * AuraShield Agent - AI for security and threat analysis.
     * TODO: Adding this based on AuraShieldAgent.kt creation, was not in original list.
     */
    AURASHIELD // Added based on previously created agent
}
