package dev.aurakai.auraframefx.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.aurakai.auraframefx.ai.services.NeuralWhisper
import dev.aurakai.auraframefx.model.AgentMessage
import dev.aurakai.auraframefx.model.AgentType
import dev.aurakai.auraframefx.model.ConversationState
import dev.aurakai.auraframefx.model.requests.AiRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

// --- Placeholder AI Service Interfaces/Classes (if not defined elsewhere) ---
// These are based on constructor parameters. User needs to ensure actual implementations exist.
interface AuraAIService {
    fun processRequest(request: AiRequest): StateFlow<AgentMessage> // Example signature
}

interface KaiAIService {
    fun processRequest(request: AiRequest): StateFlow<AgentMessage> // Example signature
}

interface CascadeAIService {
    fun processRequest(request: AiRequest): StateFlow<AgentMessage> // Example signature
}
// --- End Placeholder AI Services ---

@Singleton // Or appropriate Hilt scope
class ConferenceRoomViewModel @Inject constructor(
    private val auraService: AuraAIService, // Using placeholder interface
    private val kaiService: KaiAIService,     // Using placeholder interface
    private val cascadeService: CascadeAIService, // Using placeholder interface
    private val neuralWhisper: NeuralWhisper,
) : ViewModel() {

    private val TAG = "ConfRoomViewModel"

    private val _messages = MutableStateFlow<List<AgentMessage>>(emptyList())
    val messages: StateFlow<List<AgentMessage>> = _messages

    private val _activeAgents = MutableStateFlow(setOf<AgentType>())
    val activeAgents: StateFlow<Set<AgentType>> = _activeAgents

    private val _selectedAgent = MutableStateFlow<AgentType>(AgentType.AURA) // Default to AURA
    val selectedAgent: StateFlow<AgentType> = _selectedAgent

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording

    private val _isTranscribing = MutableStateFlow(false)
    val isTranscribing: StateFlow<Boolean> = _isTranscribing

    init {
        viewModelScope.launch {
            neuralWhisper.conversationState.collect { state ->
                when (state) {
                    is ConversationState.Responding -> {
                        _messages.update { current ->
                            current + AgentMessage(
                                content = state.responseText ?: "...",
                                sender = AgentType.NEURAL_WHISPER, // Or AURA/GENESIS depending on final source
                                timestamp = System.currentTimeMillis(),
                                confidence = 1.0f // Placeholder confidence
                            )
                        }
                        Log.d(TAG, "NeuralWhisper responded: ${state.responseText}")
                    }

                    is ConversationState.Processing -> {
                        Log.d(TAG, "NeuralWhisper processing: ${state.partialTranscript}")
                        // Optionally update UI to show "Agent is typing..." or similar
                    }

                    is ConversationState.Error -> {
                        Log.e(TAG, "NeuralWhisper error: ${state.message}")
                        _messages.update { current ->
                            current + AgentMessage(
                                content = "Error: ${state.message}",
                                sender = AgentType.NEURAL_WHISPER, // Or a system error agent
                                timestamp = System.currentTimeMillis(),
                                confidence = 0.0f
                            )
                        }
                    }

                    else -> {
                        Log.d(TAG, "NeuralWhisper state: $state")
                    }
                }
            }
        }
    }

    // This `sendMessage` was marked with `override` in user's snippet, suggesting an interface.
    // For now, assuming it's a direct method. If there's a base class/interface, it should be added.
    /*override*/ suspend fun sendMessage(message: String, sender: AgentType) {
        val responseFlow: StateFlow<AgentMessage>? = when (sender) {
            AgentType.AURA -> auraService.processRequest(AiRequest(message, "text"))
            AgentType.KAI -> kaiService.processRequest(AiRequest(message, message))
            AgentType.CASCADE -> cascadeService.processRequest(AiRequest(message, "context"))
            AgentType.USER -> {
                _messages.update { current ->
                    current + AgentMessage(
                        content = message,
                        sender = AgentType.USER,
                        timestamp = System.currentTimeMillis(),
                        confidence = 1.0f
                    )
                }
                neuralWhisper.shareContextWithKai(message)
                return // Exit, response via NeuralWhisper's state flow
            }

            else -> {
                Log.e(TAG, "Unsupported sender type: $sender")
                null // Return null for unsupported types
            }
        }

        responseFlow?.let { flow ->
            viewModelScope.launch {
                try {
                    val responseMessage =
                        flow.first() // Assuming processRequest returns a Flow/StateFlow
                    _messages.update { current ->
                        current + AgentMessage(
                            content = responseMessage.content,
                            sender = sender, // This should be the AI agent that responded, not the original sender
                            timestamp = System.currentTimeMillis(),
                            confidence = responseMessage.confidence
                        )
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error processing AI response from $sender: ${e.message}", e)
                    _messages.update { current ->
                        current + AgentMessage(
                            content = "Error from ${sender.name}: ${e.message}", // Corrected to sender.name
                            sender = AgentType.GENESIS, // Or a specific error agent
                            timestamp = System.currentTimeMillis(),
                            confidence = 0.0f
                        )
                    }
                }
            }
        }
    }

    // This `toggleAgent` was marked with `override` in user's snippet.
    /*override*/ fun toggleAgent(agent: AgentType) {
        _activeAgents.update { current ->
            if (current.contains(agent)) {
                current - agent
            } else {
                current + agent
            }
        }
    }

    fun selectAgent(agent: AgentType) {
        _selectedAgent.value = agent
    }

    fun toggleRecording() {
        if (_isRecording.value) {
            val result = neuralWhisper.stopRecording() // stopRecording now returns a string status
            Log.d(TAG, "Stopped recording. Status: $result")
            // isRecording state will be updated by NeuralWhisper's conversationState or directly
            _isRecording.value = false // Explicitly set here based on action
        } else {
            val started = neuralWhisper.startRecording()
            if (started) {
                Log.d(TAG, "Started recording.")
                _isRecording.value = true
            } else {
                Log.e(
                    TAG,
                    "Failed to start recording (NeuralWhisper.startRecording returned false)."
                )
                // Optionally update UI with error state
            }
        }
    }

    fun toggleTranscribing() {
        // For beta, link transcribing state to recording state or a separate logic if needed.
        // User's snippet implies this might be a simple toggle for now.
        _isTranscribing.update { !it } // Simple toggle
        Log.d(TAG, "Transcribing toggled to: ${_isTranscribing.value}")
        // If actual transcription process needs to be started/stopped in NeuralWhisper:
        // if (_isTranscribing.value) neuralWhisper.startTranscription() else neuralWhisper.stopTranscription()
    }
}
