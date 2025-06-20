package dev.aurakai.auraframefx.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "agent_memory")
public data class AgentMemoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    public val agentType: String,
    public val memory: String,
    public val timestamp: Long,
)
