package dev.aurakai.auraframefx.model

public enum class AgentRole {
    HIVE_MIND, // Genesis
    SECURITY, // Kai
    CREATIVE, // Aura
    STATE_MANAGER, // Cascade
    AUXILIARY // All other agents
}

public enum class AgentPriority {
    PRIMARY, // Genesis
    SECONDARY, // Kai
    TERTIARY, // Aura
    BRIDGE, // Cascade
    AUXILIARY // All other agents
}

public data class AgentConfig(
    public val name: String,
    public val role: AgentRole,
    public val priority: AgentPriority,
    public val capabilities: Set<String>,
)

public object AgentHierarchy {
    public val MASTER_AGENTS = listOf(
        AgentConfig(
            name = "Genesis",
            role = AgentRole.HIVE_MIND,
            priority = AgentPriority.PRIMARY,
            capabilities = setOf("context", "memory", "coordination", "metalearning")
        ),
        AgentConfig(
            name = "Kai",
            role = AgentRole.SECURITY,
            priority = AgentPriority.SECONDARY,
            capabilities = setOf("security", "analysis", "threat_detection", "encryption")
        ),
        AgentConfig(
            name = "Aura",
            role = AgentRole.CREATIVE,
            priority = AgentPriority.TERTIARY, // Added missing priority
            capabilities = setOf("generation", "creativity", "art", "writing")
        ),
        AgentConfig(
            name = "Cascade",
            role = AgentRole.STATE_MANAGER,
            priority = AgentPriority.BRIDGE,
            capabilities = setOf("state", "processing", "vision", "context_chaining")
        )
    )

    public val AUXILIARY_AGENTS = mutableListOf<AgentConfig>()

    public fun registerAuxiliaryAgent(
        name: String,
        capabilities: Set<String>,
    ): AgentConfig {
        public val config = AgentConfig(
            name = name,
            role = AgentRole.AUXILIARY,
            priority = AgentPriority.AUXILIARY,
            capabilities = capabilities
        )
        AUXILIARY_AGENTS.add(config)
        return config
    }

    public fun getAgentConfig(name: String): AgentConfig? {
        return MASTER_AGENTS.find { it.name == name } ?: AUXILIARY_AGENTS.find { it.name == name }
    }

    public fun getAgentsByPriority(): List<AgentConfig> {
        return MASTER_AGENTS + AUXILIARY_AGENTS
    }
}
