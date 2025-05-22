package dev.aurakai.auraframefx.util

import kotlinx.serialization.json.Json

object JsonUtils {
    private val json = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
    }

    inline fun <reified T> fromJson(jsonString: String): T {
        return json.decodeFromString(jsonString)
    }

    inline fun <reified T> toJson(obj: T): String {
        return json.encodeToString(obj)
    }
}
