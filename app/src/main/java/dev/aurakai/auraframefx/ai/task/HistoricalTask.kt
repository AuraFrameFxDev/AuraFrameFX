package dev.aurakai.auraframefx.ai.task

import dev.aurakai.auraframefx.model.AgentType
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Data class representing a historical task executed by an agent
 */
@Serializable
public data class HistoricalTask(
    public val agentType: AgentType,
    public val description: String,
    public val timestamp: Long = System.currentTimeMillis(),
) {
    public fun getFormattedTime(): String {
        public val formatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return formatter.format(Date(timestamp))
    }

    override fun toString(): String {
        return "[${getFormattedTime()}] ${agentType.name}: $description"
    }
}
