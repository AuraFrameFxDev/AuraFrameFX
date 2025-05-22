package dev.aurakai.auraframefx.ai

// import kotlinx.serialization.encodeToString // Uncomment if used
// import kotlinx.serialization.decodeFromString // Uncomment if used
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds
import dev.aurakai.auraframefx.ai.models.ContextNode
import dev.aurakai.auraframefx.ai.models.ContextChain
import dev.aurakai.auraframefx.ai.models.LearningEvent

// TODO: REVIEW - Ensure kotlinx.serialization Gradle plugin is applied in your build.gradle.kts
// TODO: REVIEW - For Map<String, Any> in data classes:
// Ensure values are restricted to basic serializable types (String, Int, Boolean, List/Map of serializable types)
// OR implement custom serializers if complex/non-serializable objects are needed.

@HiltViewModel // CHANGED: Modern Hilt annotation
class NeuralWhisperAgent @Inject constructor(
    // CHANGED: Modern Hilt constructor injection
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val _contextChain = MutableStateFlow(ContextChain(emptyList()))
    val contextChain: StateFlow<ContextChain> = _contextChain

    private val _learningHistory = MutableStateFlow<List<LearningEvent>>(emptyList())
    val learningHistory: StateFlow<List<LearningEvent>> = _learningHistory

    private val _activeContexts = MutableStateFlow<Map<String, ContextNode>>(emptyMap())
    val activeContexts: StateFlow<Map<String, ContextNode>> = _activeContexts

    init {
        startContextMonitoring()
    }

    private fun startContextMonitoring() {
        viewModelScope.launch {
            while (true) { // This loop will be cancelled when viewModelScope is cancelled in onCleared()
                try {
                    val newContext = captureContext()
                    updateContextChain(newContext)
                    analyzeContextChain()
                    delay(1.seconds) // Make sure kotlin.time.Duration.Companion.seconds is imported
                } catch (e: Exception) {
                    // Check for specific exceptions like CancellationException if needed
                    if (e is kotlinx.coroutines.CancellationException) {
                        Log.d("NeuralWhisperAgent", "Context monitoring cancelled.")
                        throw e // Re-throw CancellationException
                    }
                    Log.e("NeuralWhisperAgent", "Context monitoring error: ${e.message}", e)
                    // Consider a longer delay or breaking the loop on persistent errors
                }
            }
        }
    }

    private suspend fun captureContext(): ContextNode {
        // TODO: Implement actual context capture logic
        // For now, returning a placeholder
        return ContextNode(
            id = "node_${System.currentTimeMillis()}",
            content = "System context at ${System.currentTimeMillis()}",
            metadata = mapOf(
                "timestamp" to System.currentTimeMillis().toString()
            ) // Ensure metadata values are serializable (e.g., String)
        )
    }

    private fun updateContextChain(newNode: ContextNode) {
        _contextChain.update { currentChain ->
            val newNodes = currentChain.nodes + newNode
            val newRelationships = currentChain.relationships.toMutableMap().apply {
                // Ensure the new node is added as a key, even if its children list is initially empty
                put(newNode.id, emptyList())
            }
            // Potentially link newNode to a parent if applicable, e.g., if nodes have a parentId
            // For now, just adding it as a new root or disconnected node in relationships
            ContextChain(
                newNodes,
                newRelationships,
                currentChain.metadata
            ) // Pass existing metadata
        }
    }

    private fun analyzeContextChain() {
        val currentChain = _contextChain.value
        // Ensure insights map values are serializable (e.g., String)
        val insights = analyzePatterns(currentChain)
        recordLearningEvent(currentChain, insights)
    }

    private fun analyzePatterns(chain: ContextChain): Map<String, String> { // Changed return type for simplicity
        // TODO: Implement pattern analysis logic
        // Ensure returned map values are serializable
        return mapOf(
            "pattern_found" to "false",
            "confidence" to "0.0",
            "timestamp" to System.currentTimeMillis().toString()
        )
    }

    private fun recordLearningEvent(
        chain: ContextChain,
        insights: Map<String, String>,
    ) { // Changed insights type
        val event = LearningEvent(
            id = "event_${System.currentTimeMillis()}",
            context = chain,
            insights = insights
            // metadata can be added if needed, ensure it's serializable
        )
        _learningHistory.update { currentHistory ->
            // Add new event and then take the last 100. This is slightly more efficient.
            (currentHistory + event).takeLast(100)
        }
    }

    fun addContextNode(node: ContextNode) {
        _activeContexts.update { currentActiveContexts ->
            currentActiveContexts + (node.id to node)
        }
        // Optionally, immediately update the main context chain as well
        // updateContextChain(node)
    }

    fun getContextChain(): ContextChain = _contextChain.value

    fun learnFromContextChain() {
        val chain = _contextChain.value
        val insights = analyzePatterns(chain)
        recordLearningEvent(chain, insights)
    }

    fun learnFromExperience(experience: String) {
        val node = ContextNode(
            id = "experience_${System.currentTimeMillis()}",
            content = experience,
            metadata = mapOf("type" to "experience") // Ensure metadata values are serializable
        )
        addContextNode(node)
        // Decide if learning should happen immediately or be batched
        learnFromContextChain()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel() // Cancel all coroutines started in this ViewModel's scope
        Log.d("NeuralWhisperAgent", "ViewModel cleared, context monitoring stopped.")
    }
}