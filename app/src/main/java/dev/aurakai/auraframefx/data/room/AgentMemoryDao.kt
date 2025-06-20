package dev.aurakai.auraframefx.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
public interface AgentMemoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: AgentMemoryEntity)

    @Query("SELECT * FROM agent_memory WHERE agentType = :agentType ORDER BY timestamp DESC")
    public fun getMemoriesForAgent(agentType: String): Flow<List<AgentMemoryEntity>>
}
