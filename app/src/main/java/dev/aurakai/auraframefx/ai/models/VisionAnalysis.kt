package dev.aurakai.auraframefx.ai.models

import kotlinx.serialization.Serializable

/**
 * Represents the complete analysis of visual content including objects, scenes, and emotional context.
 */
@Serializable
data class VisionAnalysis(
    /** List of detected objects in the scene */
    val objects: List<ObjectDetection> = emptyList(),
    
    /** Analysis of the overall scene */
    val scene: SceneUnderstanding,
    
    /** Emotional analysis of the visual content */
    val emotionalAnalysis: EmotionalAnalysis,
    
    /** Timestamp of when the analysis was performed */
    val timestamp: Long = System.currentTimeMillis(),
    
    /** Confidence score of the overall analysis (0.0 to 1.0) */
    val confidence: Float
)

/**
 * Represents a detected object in the visual content
 */
@Serializable
data class ObjectDetection(
    /** Name/type of the detected object */
    val name: String,
    
    /** Confidence score of the detection (0.0 to 1.0) */
    val confidence: Float,
    
    /** Bounding box coordinates (x, y, width, height) normalized to [0,1] */
    val boundingBox: BoundingBox,
    
    /** Optional: Additional attributes of the object */
    val attributes: Map<String, String> = emptyMap()
)

/**
 * Represents the bounding box of a detected object
 */
@Serializable
data class BoundingBox(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float
)

/**
 * Represents the understanding of a scene
 */
@Serializable
data class SceneUnderstanding(
    /** Type of the scene */
    val type: SceneType,
    
    /** Description of the scene */
    val description: String,
    
    /** Confidence score of the scene understanding (0.0 to 1.0) */
    val confidence: Float,
    
    /** List of identified activities in the scene */
    val activities: List<String> = emptyList()
)

/**
 * Represents the emotional analysis of visual content
 */
@Serializable
data class EmotionalAnalysis(
    /** Dominant emotion detected */
    val dominantEmotion: Mood,
    
    /** Confidence score of the emotion detection (0.0 to 1.0) */
    val confidence: Float,
    
    /** Emotion intensity (0.0 to 1.0) */
    val intensity: Float,
    
    /** Additional emotional context */
    val context: String = ""
)

/**
 * Represents different types of scenes
 */
@Serializable
enum class SceneType {
    INDOOR,
    OUTDOOR,
    URBAN,
    NATURE,
    HOME,
    OFFICE,
    VEHICLE,
    SOCIAL_GATHERING,
    SPORTING_EVENT,
    CONCERT,
    UNKNOWN
}

/**
 * Represents different mood states
 */
@Serializable
enum class Mood {
    HAPPY,
    SAD,
    ANGRY,
    SURPRISED,
    FEARFUL,
    DISGUSTED,
    NEUTRAL,
    EXCITED,
    RELAXED,
    CONFUSED,
    UNKNOWN
}

/**
 * Represents a learning event for the AI system
 */
@Serializable
data class LearningEvent(
    /** Type of learning event */
    val type: LearningEventType,
    
    /** Timestamp of the event */
    val timestamp: Long = System.currentTimeMillis(),
    
    /** Additional metadata about the event */
    val metadata: Map<String, String> = emptyMap(),
    
    /** Confidence score of the learning (0.0 to 1.0) */
    val confidence: Float = 1.0f
)

/**
 * Types of learning events
 */
@Serializable
enum class LearningEventType {
    OBJECT_RECOGNITION,
    SCENE_UNDERSTANDING,
    EMOTION_DETECTION,
    USER_FEEDBACK,
    SYSTEM_CORRECTION,
    PATTERN_RECOGNITION,
    CONTEXT_UPDATE,
    PREFERENCE_LEARNING
}

/**
 * Represents the emotional state of an entity
 */
@Serializable
enum class EmotionState {
    CALM,
    EXCITED,
    FRUSTRATED,
    CONFUSED,
    SATISFIED,
    DISAPPOINTED,
    OPTIMISTIC,
    PESSIMISTIC,
    NEUTRAL,
    UNKNOWN
}
