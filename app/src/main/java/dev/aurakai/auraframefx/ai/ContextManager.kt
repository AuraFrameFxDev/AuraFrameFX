package dev.aurakai.auraframefx.ai

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.cloud.generativeai.GenerativeModel
import com.google.firebase.vertexai.VertexAI
import kotlinx.coroutines.CoroutineScope // Added import
import kotlinx.coroutines.Dispatchers // Added import
import kotlinx.coroutines.SupervisorJob // Added import
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch // Added import
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException // Added import
import kotlinx.serialization.encodeToString // Added import
import kotlinx.serialization.json.Json // Added import
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

import kotlinx.serialization.Serializable // Added import

/**
 * AIContext represents the shared context between Aura, Kai, and NeuralWhisper
 * This context is used to maintain conversation history and emotional state
 */
@Serializable // Added annotation
data class AIContext(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    // Assumes EmotionState will be made @Serializable
    // Assumes SecurityContext (and its inner SecurityMetrics) will be made @Serializable
    val userContext: String = "",
    val auraContext: String = "",
    val kaiContext: String = "",
    val vertexContext: String = "",
    val emotion: EmotionState = EmotionState.Neutral, // Assumes EmotionState is @Serializable
    val securityMetrics: SecurityContext.SecurityMetrics? = null, // Changed from SecurityContext to SecurityMetrics
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
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true } // Added Json instance
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob()) // Added CoroutineScope

    private val _currentContext = MutableStateFlow(AIContext())
    val currentContext: StateFlow<AIContext> = _currentContext.asStateFlow()

    private val aiContextPreferenceKey = stringPreferencesKey("ai_context_json") // New single key

    init {
        scope.launch { // Launch coroutine for loading
            loadPersistedContext()
        }
    }

    private suspend fun loadPersistedContext() {
        try {
            val contextJsonString = datastore.data.first()[aiContextPreferenceKey]
            if (contextJsonString != null && contextJsonString.isNotBlank()) {
                val loadedContext = json.decodeFromString<AIContext>(contextJsonString)
                _currentContext.update { loadedContext }
            } else {
                _currentContext.update { AIContext() } // Initialize with default if no data
            }
        } catch (e: SerializationException) {
            Timber.e(e, "Failed to decode AIContext from DataStore.")
            _currentContext.update { AIContext() } // Initialize with default on error
        } catch (e: Exception) { // Catch other potential exceptions like NoSuchElementException if key not found
            Timber.e(e, "Failed to load AIContext from DataStore.")
            _currentContext.update { AIContext() } // Initialize with default on error
        }
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
        securityMetrics: SecurityContext.SecurityMetrics? = null, // Changed from SecurityContext
    ) {
        _currentContext.update { current ->
            val updated = current.copy(
                userContext = userContext ?: current.userContext,
                auraContext = auraContext ?: current.auraContext,
                kaiContext = kaiContext ?: current.kaiContext,
                vertexContext = vertexContext ?: current.vertexContext,
                emotion = emotion ?: current.emotion,
                securityMetrics = securityMetrics ?: current.securityMetrics, // Changed field name
                timestamp = System.currentTimeMillis()
            )
            scope.launch { // Launch coroutine for saving
                saveContext(updated)
            }
            updated
        }
    }

    /**
     * Save context to DataStore
     */
    private suspend fun saveContext(context: AIContext) {
        datastore.edit { prefs ->
            val contextJsonString = json.encodeToString(context)
            prefs[aiContextPreferenceKey] = contextJsonString
        }
    }

    /**
     * Clear all context
     */
    fun clearContext() {
        val clearedContext = AIContext()
        _currentContext.update { clearedContext }
        scope.launch { // Launch coroutine for saving
            saveContext(clearedContext)
        }
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
            current.securityMetrics?.let { metrics -> // Changed to securityMetrics
                // Optionally serialize metrics to a string summary or use specific fields
                // For now, let's just indicate its presence or a summary
                // This part depends on how SecurityMetrics should be represented in the prompt
                contextBuilder.append("Security metrics available.\n") // Placeholder for actual metrics representation
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
