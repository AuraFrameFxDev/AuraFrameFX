package dev.aurakai.auraframefx.model.agent_states

// TODO: Define actual properties for these states.
// TODO: Classes reported as unused or need implementation. Ensure these are utilized by CascadeAgent.

public data class VisionState(
    public val lastObservation: String? = null,
    public val objectsDetected: List<String> = emptyList(),
    // Add other relevant vision state properties
)

public data class ProcessingState(
    public val currentStep: String? = null,
    public val progressPercentage: Float = 0.0f,
    public val isError: Boolean = false,
    // Add other relevant processing state properties
)
