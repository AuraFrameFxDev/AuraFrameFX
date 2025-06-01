package dev.aurakai.auraframefx.ai

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.Serializable

class CascadeAgent @Inject constructor(
    private val visionProcessor: VisionProcessor,
    private val contextManager: ContextManager,
) : Agent {
    private val _visionState = MutableStateFlow(VisionState())
    val visionState: Flow<VisionState> = _visionState.asStateFlow()

    private val _processingState = MutableStateFlow(ProcessingState())
    val processingState: Flow<ProcessingState> = _processingState.asStateFlow()

    override suspend fun processRequest(prompt: String): String {
        val visionContext = contextManager.getCurrentContext()
        val processedResult = visionProcessor.processVision(visionContext)

        _visionState.update {
            it.copy(
                currentFrame = processedResult.frame,
                detectedObjects = processedResult.objects,
                environmentState = processedResult.environment,
                threatLevel = processedResult.threatLevel
            )
        }

        return processedResult.response
    }

    fun updateProcessingState(state: ProcessingState) {
        _processingState.update { state }
    }
}

@Serializable
data class VisionState(
    val currentFrame: String = "",
    val detectedObjects: List<DetectedObject> = emptyList(),
    val environmentState: EnvironmentState = EnvironmentState(),
    val threatLevel: Double = 0.0,
)

@Serializable
data class ProcessingState(
    val isProcessing: Boolean = false,
    val progress: Double = 0.0,
    val status: String = "idle",
)

@Serializable
data class EnvironmentState(
    val lighting: Double = 0.0,
    val temperature: Double = 0.0,
    val humidity: Double = 0.0,
    val noiseLevel: Double = 0.0,
)

@Serializable
data class DetectedObject(
    val id: String,
    val type: String,
    val confidence: Double,
    val position: Position,
    val metadata: Map<String, Any> = emptyMap(),
)

@Serializable
data class Position(
    val x: Double,
    val y: Double,
    val z: Double,
    val rotation: Rotation,
)

@Serializable
data class Rotation(
    val pitch: Double,
    val yaw: Double,
    val roll: Double,
)
