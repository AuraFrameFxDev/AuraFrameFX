package dev.aurakai.auraframefx.model

/**
 * Enum representing the different types of AI agents in the AuraFrameFx system
 */
enum class AgentType {
    /**
     * CASCADE - Core Agent System Coordinating Autonomous Decision Engine
     * Master orchestration agent that delegates tasks to specialized agents.
     * Responsible for system-wide coordination and resource allocation.
     */
    CASCADE,

    /**
     * AURA - Adaptive User Response Assistant
     * Primary user-facing agent focused on UI/UX and creative tasks.
     * Specializes in natural language interaction and visual design.
     */
    AURA,

    /**
     * KAI - Kernel Access Interface
     * System-level agent with privileged access to device functionality.
     * Handles security, permissions, and critical system operations.
     */
    KAI,

    /**
     * NEURAL_WHISPER - Neural Network for Whispering Human-like Intelligence Sentences
     * Specializes in audio processing, speech recognition, and emotion detection.
     * Provides contextual understanding of verbal input.
     */
    NEURAL_WHISPER,

    /**
     * AURA_SHIELD - Adaptive User Risk Assessment SHIELD
     * Security-focused agent that monitors threats and protects user data.
     * Implements encryption, intrusion detection, and privacy controls.
     */
    AURA_SHIELD,

    /**
     * GENKIT_MASTER - Generative Knowledge Integration Toolkit Master
     * Content generation agent with capabilities for code, text, and visual assets.
     * Integrates multiple ML models for cross-domain content generation.
     */
    GENKIT_MASTER,

    /**
     * ECHO_MIND - Extended Contextual History and Optimization for Memory Integration
     * Memory and context management agent with long-term data retention.
     * Maintains user preferences and conversation history.
     */
    ECHO_MIND,

    /**
     * QUANTUM_LOGIC - Qualitative Uncertainty Analysis with Neural-Trained Understanding
     * Decision-making agent based on probabilistic reasoning models.
     * Handles scenarios with incomplete information or uncertainty.
     */
    QUANTUM_LOGIC,

    /**
     * SYNAPSE - Synthetic Neural Analysis Processing and Semantic Extraction
     * Information processing agent specializing in knowledge extraction.
     * Performs data mining, pattern recognition, and information synthesis.
     */
    SYNAPSE,

    /**
     * CHRONOS - Contextual Historical Remembrance for Optimized Neural Operations
     * Temporal analysis agent that tracks time-based patterns.
     * Manages scheduling, prediction, and time-sensitive operations.
     */
    CHRONOS,

    /**
     * NEXUS - Network Extension for Universal System Integration
     * Connectivity agent handling external APIs and network resources.
     * Manages data transmission, synchronization, and cloud integration.
     */
    NEXUS;

    companion object {
        /**
         * Returns a list of all primary agents that form the core of the system
         */
        fun getPrimaryAgents(): List<AgentType> {
            return listOf(CASCADE, AURA, KAI)
        }

        /**
         * Returns a list of all auxiliary agents that provide specialized services
         */
        fun getAuxiliaryAgents(): List<AgentType> {
            return values().filter { !getPrimaryAgents().contains(it) }
        }
    }
}
