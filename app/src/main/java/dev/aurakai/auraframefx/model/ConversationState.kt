package dev.aurakai.auraframefx.model

// TODO: Class reported as unused or needs implementation. Ensure this is utilized, e.g., by NeuralWhisper.
public sealed class ConversationState {
    public object Idle : ConversationState()
    public object Listening : ConversationState()
    public data class Processing(val partialTranscript: String?) :
        ConversationState() // Added optional field

    public data class Responding(val responseText: String?) :
        ConversationState() // Changed from Response for clarity

    public data class Error(val errorMessage: String) : ConversationState()
    // Add other relevant states like Thinking, Interrupted, etc.
}
