package dev.aurakai.auraframefx.ai.task

import dev.aurakai.auraframefx.model.AgentType
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

@Serializable
class AnyValueSerializer : KSerializer<Any> {
    override val descriptor = JsonPrimitive.serializer().descriptor

    /**
     * Serializes an arbitrary value to JSON, supporting strings, numbers, booleans, and maps with string keys.
     *
     * Strings, numbers, and booleans are encoded as their respective JSON primitives. Maps are encoded as JSON objects with string keys and primitive values. Other types are serialized as their string representation.
     *
     * @throws IllegalStateException if the encoder is not a JsonEncoder.
     */
    override fun serialize(encoder: Encoder, value: Any) {
        val jsonEncoder = encoder as? JsonEncoder ?: throw IllegalStateException("This encoder is not a JsonEncoder")
        when (value) {
            is String -> jsonEncoder.encodeString(value)
            is Number -> jsonEncoder.encodeJsonElement(JsonPrimitive(value))
            is Boolean -> jsonEncoder.encodeBoolean(value)
            is Map<*, *> -> {
                val stringKeyMap = value.entries.associate { entry ->
                    val keyString = entry.key?.toString() ?: "null" // Ensure key is a String, handle null keys
                    val valueJsonElement: JsonElement = when (val v = entry.value) {
                        is String -> JsonPrimitive(v)
                        is Number -> JsonPrimitive(v)
                        is Boolean -> JsonPrimitive(v)
                        // If you anticipate nested maps or lists, they'd need more handling here
                        // or rely on a more general Json.encodeToJsonElement approach if possible.
                        else -> JsonPrimitive(v?.toString()) // Fallback for other types
                    }
                    keyString to valueJsonElement
                }
                jsonEncoder.encodeJsonElement(JsonObject(stringKeyMap))
            }
            else -> jsonEncoder.encodeString(value.toString())
        }
    }


    /**
     * Deserializes a JSON element into a Kotlin value of type `Any`.
     *
     * Converts JSON primitives to their corresponding Kotlin types (`String`, `Boolean`, `Double`, or `Long`), JSON objects to maps with stringified values, and other JSON elements to their string representation.
     *
     * @return The deserialized value as a Kotlin `Any` type.
     * @throws IllegalStateException if the provided decoder is not a `JsonDecoder`.
     */
    override fun deserialize(decoder: Decoder): Any {
        val jsonDecoder = decoder as? JsonDecoder ?: throw IllegalStateException("This decoder is not a JsonDecoder")
        val element = jsonDecoder.decodeJsonElement()
        return when (element) {
            is JsonPrimitive -> {
                when {
                    element.isString -> element.content
                    element.booleanOrNull != null -> element.boolean
                    element.doubleOrNull != null -> element.double
                    element.longOrNull != null -> element.long
                    else -> element.content
                }
            }
            is JsonObject -> element.mapValues { it.value.toString() }
            else -> element.toString()
        }
    }
}

@Serializable
data class Task(
    val id: String = "task_${Clock.System.now().toEpochMilliseconds()}",
    val timestamp: Instant = Clock.System.now(),
    val priority: TaskPriority = TaskPriority.NORMAL,
    val urgency: TaskUrgency = TaskUrgency.MEDIUM,
    val importance: TaskImportance = TaskImportance.MEDIUM,
    val context: String,
    val content: String,
    @Contextual
    val metadata: Map<String, @Contextual Any> = emptyMap(),
    val status: TaskStatus = TaskStatus.PENDING,
    val assignedAgents: Set<AgentType> = emptySet(),
    val requiredAgents: Set<AgentType> = emptySet(),
    val completionTime: Instant? = null,
    val estimatedDuration: Long = 0,
    val dependencies: Set<String> = emptySet(),
)

@Serializable
data class TaskDependency(
    val taskId: String,
    val dependencyId: String,
    val type: DependencyType,
    val priority: TaskPriority,
    @Contextual
    val metadata: Map<String, @Contextual Any> = emptyMap(),
)

@Serializable
data class TaskPriority(
    val value: Float,
    val reason: String,
    @Contextual
    val metadata: Map<String, @Contextual Any> = emptyMap(),
) {
    companion object {
        val CRITICAL = TaskPriority(1.0f, "Critical system task")
        val HIGH = TaskPriority(0.8f, "High priority task")
        val NORMAL = TaskPriority(0.5f, "Normal priority task")
        val LOW = TaskPriority(0.3f, "Low priority background task")
        val MINOR = TaskPriority(0.1f, "Minor maintenance task")
    }
}

@Serializable
data class TaskUrgency(
    val value: Float,
    val reason: String,
    @Contextual
    val metadata: Map<String, @Contextual Any> = emptyMap(),
) {
    companion object {
        val IMMEDIATE = TaskUrgency(1.0f, "Immediate attention required")
        val HIGH = TaskUrgency(0.8f, "High urgency")
        val NORMAL = TaskUrgency(0.5f, "Normal urgency")
        val LOW = TaskUrgency(0.3f, "Low urgency")
        val BACKGROUND = TaskUrgency(0.1f, "Background task")
    }
}

@Serializable
data class TaskImportance(
    val value: Float,
    val reason: String,
    @Contextual
    val metadata: Map<String, @Contextual Any> = emptyMap(),
) {
    companion object {
        val CRITICAL = TaskImportance(1.0f, "Critical system task")
        val HIGH = TaskImportance(0.8f, "High importance task")
        val NORMAL = TaskImportance(0.5f, "Normal importance task")
        val LOW = TaskImportance(0.3f, "Low importance task")
        val MINOR = TaskImportance(0.1f, "Minor task")
    }
}

@Serializable
enum class TaskStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    FAILED,
    CANCELLED,
    BLOCKED,
    WAITING
}

@Serializable
enum class DependencyType {
    BLOCKING,
    SEQUENTIAL,
    PARALLEL,
    OPTIONAL,
    SOFT
}
