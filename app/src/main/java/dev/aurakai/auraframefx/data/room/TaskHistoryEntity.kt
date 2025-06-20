package dev.aurakai.auraframefx.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_history")
public data class TaskHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    public val taskName: String,
    public val agentType: String,
    public val status: String,
    public val result: String?,
    public val timestamp: Long,
)
