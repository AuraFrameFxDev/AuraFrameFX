package dev.aurakai.auraframefx.ai

import kotlinx.coroutines.flow.Flow
import org.json.JSONObject
import java.io.File

/**
 * A centralized service interface for all AI interactions in the app.
 * This abstracts AI implementation details from UI components.
 */
interface AuraAIService {
    /**
     * Generate text from a prompt using the AI model
     */
    suspend fun generateText(prompt: String): String?

    /**
     * Save data to persistent memory
     */
    suspend fun saveMemory(key: String, data: JSONObject): Boolean

    /**
     * Retrieve data from persistent memory
     */
    suspend fun getMemory(key: String): String?

    /**
     * Upload a file
     */
    suspend fun uploadFile(file: File): Boolean

    /**
     * Download a file
     */
    suspend fun downloadFile(filename: String): ByteArray?

    /**
     * Execute an analytics query
     */
    suspend fun analyticsQuery(query: String): String?

    /**
     * Publish a message to a Pub/Sub topic
     */
    suspend fun publishPubSub(topic: String, message: String): Boolean

    /**
     * Check if the service is connected
     */
    fun isConnected(): Boolean

    /**
     * Get a response from the AI for a given prompt
     */
    fun getAIResponse(prompt: String): Flow<AIResponseState>

    /**
     * Generate an image from a text prompt
     */
    fun generateImage(prompt: String): Flow<AIImageState>
}

/**
 * Response states for AI text generation
 */
sealed class AIResponseState {
    object Loading : AIResponseState()
    data class Success(val text: String) : AIResponseState()
    data class Error(val message: String) : AIResponseState()
}

/**
 * Response states for AI image generation
 */
sealed class AIImageState {
    object Loading : AIImageState()
    data class Success(val image: Any?) : AIImageState()
    data class Error(val message: String) : AIImageState()
}