package dev.aurakai.auraframefx.ai

import kotlinx.coroutines.flow.Flow

/**
 * Interface defining the contract for interacting with Google's Vertex AI services.
 * Provides methods for generating content and managing chat sessions.
 */
interface VertexAIClient {
    
    /**
     * Generates content based on the provided prompt.
     * @param prompt The input prompt for content generation
     * @return Generated content as a String, or null if generation fails
     */
    suspend fun generateContent(prompt: String): String?
    
    /**
     * Generates a stream of content based on the provided prompt.
     * @param prompt The input prompt for content generation
     * @return Flow of generated content chunks
     */
    fun generateContentStream(prompt: String): Flow<String>
    
    /**
     * Starts a chat session with the AI model.
     * @param systemPrompt Initial system prompt to set the chat context
     * @return Response from the AI model
     */
    suspend fun chat(systemPrompt: String): String
    
    /**
     * Starts a streaming chat session with the AI model.
     * @param systemPrompt Initial system prompt to set the chat context
     * @return Flow of response chunks from the AI model
     */
    fun chatStream(systemPrompt: String): Flow<String>
    
    /**
     * Sends a message in an existing chat session.
     * @param message The user's message
     * @param sessionId Optional session ID for continuing a conversation
     * @return The AI's response
     */
    suspend fun sendMessage(message: String, sessionId: String? = null): String
    
    /**
     * Sends a message in an existing chat session with streaming response.
     * @param message The user's message
     * @param sessionId Optional session ID for continuing a conversation
     * @return Flow of response chunks from the AI model
     */
    fun sendMessageStream(message: String, sessionId: String? = null): Flow<String>
}

/**
 * Exception class for Vertex AI related errors.
 */
class VertexAIException(message: String, cause: Throwable? = null) : 
    Exception(message, cause)
