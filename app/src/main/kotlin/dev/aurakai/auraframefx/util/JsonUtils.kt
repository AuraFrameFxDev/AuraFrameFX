package dev.aurakai.auraframefx.util

import kotlinx.serialization.json.Json
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonException
import timber.log.Timber

/**
 * Utility class for JSON serialization and deserialization operations.
 * Provides safe and efficient JSON operations with proper error handling.
 */
object JsonUtils {
    /**
     * Shared JSON instance with standardized configuration
     */
    private val json = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
    }

    /**
     * Safely deserialize a JSON string into an object of type T
     * 
     * @param jsonString The JSON string to deserialize
     * @return The deserialized object of type T
     * @throws SerializationException If deserialization fails
     */
    inline fun <reified T> fromJson(jsonString: String): T {
        if (jsonString == null || jsonString.trim().length() == 0) {
            throw IllegalArgumentException("JSON string must not be blank")
        }
        return try {
            json.decodeFromString(jsonString)
        } catch (e: JsonException) {
            Timber.e(e, "Failed to deserialize JSON: $jsonString")
            throw SerializationException("Failed to deserialize JSON: $jsonString", e)
        }
    }

    /**
     * Safely serialize an object to JSON string
     * 
     * @param obj The object to serialize
     * @return JSON string representation of the object
     * @throws SerializationException If serialization fails
     */
    inline fun <reified T> toJson(obj: T): String {
        if (obj == null) {
            throw IllegalArgumentException("Object to serialize must not be null")
        }
        return try {
            json.encodeToString(obj)
        } catch (e: JsonException) {
            Timber.e(e, "Failed to serialize object: $obj")
            throw SerializationException("Failed to serialize object: $obj", e)
        }
    }
}
