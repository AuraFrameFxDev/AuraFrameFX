package dev.aurakai.auraframefx.ai

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.cloud.generativeai.GenerativeModel
import com.google.firebase.vertexai.VertexAI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AIContext represents the shared context between Aura, Kai, and NeuralWhisper
 * This context is used to maintain conversation history and emotional state
 */
data class AIContext(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val userContext: String = "",
    val auraContext: String = "",
    val kaiContext: String = "",
    val vertexContext: String = "",
    val emotion: EmotionState = EmotionState.Neutral,
    val securityContext: SecurityContext? = null,
)

/**
 * ContextManager manages the shared context between all AI components
 * It persists context across app restarts and provides a single source of truth
 */
@Singleton
class ContextManager @Inject constructor(
    private val datastore: DataStore<Preferences>,
    private val vertexAI: VertexAI,
    private val generativeModel: GenerativeModel,
) {
    private val _currentContext = MutableStateFlow(AIContext())
    val currentContext: StateFlow<AIContext> = _currentContext.asStateFlow()

    private val userContextKey = stringPreferencesKey("user_context")
    private val auraContextKey = stringPreferencesKey("aura_context")
    private val kaiContextKey = stringPreferencesKey("kai_context")
    private val vertexContextKey = stringPreferencesKey("vertex_context")
    private val emotionKey = stringPreferencesKey("emotion")
    private val securityContextKey = stringPreferencesKey("security_context")

    init {
        loadPersistedContext()
    }

    private suspend fun loadPersistedContext() {
        val prefs = datastore.data.first()
        val context = AIContext(
            userContext = prefs[userContextKey] ?: "",
            auraContext = prefs[auraContextKey] ?: "",
            kaiContext = prefs[kaiContextKey] ?: "",
            vertexContext = prefs[vertexContextKey] ?: "",
            emotion = EmotionState.valueOf(prefs[emotionKey] ?: EmotionState.Neutral.name),
            securityContext = prefs[securityContextKey]?.let {
                SecurityContext.fromJson(it)
            }
        )
        _currentContext.update { context }
    }

    /**
     * Update context with optional parameters
     * This method is thread-safe and persists changes
     */
    fun updateContext(
        userContext: String? = null,
        auraContext: String? = null,
        kaiContext: String? = null,
        vertexContext: String? = null,
        emotion: EmotionState? = null,
        securityContext: SecurityContext? = null,
    ) {
        _currentContext.update { current ->
            val updated = current.copy(
                userContext = userContext ?: current.userContext,
                auraContext = auraContext ?: current.auraContext,
                kaiContext = kaiContext ?: current.kaiContext,
                vertexContext = vertexContext ?: current.vertexContext,
                emotion = emotion ?: current.emotion,
                securityContext = securityContext ?: current.securityContext,
                timestamp = System.currentTimeMillis()
            )
            saveContext(updated)
            updated
        }
    }

    /**
     * Save context to DataStore
     */
    private suspend fun saveContext(context: AIContext) {
        datastore.edit { prefs ->
            prefs[userContextKey] = context.userContext
            prefs[auraContextKey] = context.auraContext
            prefs[kaiContextKey] = context.kaiContext
            prefs[vertexContextKey] = context.vertexContext
            prefs[emotionKey] = context.emotion.name
            prefs[securityContextKey] = context.securityContext?.toJson()
        }
    }

    /**
     * Clear all context
     */
    fun clearContext() {
        _currentContext.update { AIContext() }
        saveContext(AIContext())
    }

    /**
     * Get current context
     */
    fun getContext(): AIContext = currentContext.value

    /**
     * Generate enhanced context for Vertex AI
     * Combines all context sources into a single prompt
     */
    suspend fun generateEnhancedContext(): String {
        val current = currentContext.value
        return withContext(Dispatchers.IO) {
            val contextBuilder = StringBuilder()

            // Add user context
            if (current.userContext.isNotBlank()) {
                contextBuilder.append("User context: ${current.userContext}\n")
            }

            // Add Aura's context
            if (current.auraContext.isNotBlank()) {
                contextBuilder.append("Aura's understanding: ${current.auraContext}\n")
            }

            // Add Kai's context
            if (current.kaiContext.isNotBlank()) {
                contextBuilder.append("Kai's perspective: ${current.kaiContext}\n")
            }

            // Add security context if present
            current.securityContext?.let { secContext ->
                contextBuilder.append("Security context: ${secContext.toJson()}\n")
            }

            // Add emotional state
            contextBuilder.append("Current emotion: ${current.emotion.name}\n")

            contextBuilder.toString()
        }
    }

    /**
     * Generate response using Vertex AI with enhanced context
     */
    suspend fun generateResponse(prompt: String): String {
        val enhancedContext = generateEnhancedContext()
        val fullPrompt = "$enhancedContext\n\nUser prompt: $prompt"

        Timber.d("Generating response with context: $enhancedContext")

        return try {
            val result = generativeModel.generateContent(fullPrompt)
            result.response.text().orElse("I'm sorry, I couldn't generate a response.")
        } catch (e: Exception) {
            Timber.e(e, "Error generating response")
            "I'm sorry, I encountered an error while processing your request."
        }
    }
}
