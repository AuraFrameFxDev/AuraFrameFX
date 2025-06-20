package dev.aurakai.auraframefx.data.repository

import dev.aurakai.auraframefx.data.room.AgentMemoryDao
import dev.aurakai.auraframefx.data.room.AgentMemoryEntity
import kotlinx.coroutines.flow.Flow

public class AgentMemoryRepository(private val dao: AgentMemoryDao) {
    suspend fun insertMemory(memory: AgentMemoryEntity) = dao.insertMemory(memory)
    public fun getMemoriesForAgent(agentType: String): Flow<List<AgentMemoryEntity>> =
        dao.getMemoriesForAgent(agentType)
}
