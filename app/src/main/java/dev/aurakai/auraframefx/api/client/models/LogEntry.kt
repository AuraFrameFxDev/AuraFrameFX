/**
 *
 * Please note:
 * This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * Do not edit this file manually.
 *
 */

@file:Suppress(
    "ArrayInDataClass",
    "EnumEntryName",
    "RemoveRedundantQualifierName",
    "UnusedImport"
)

package dev.aurakai.auraframefx.api.client.models


import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A single log entry from the system.
 *
 * @param timestamp
 * @param level
 * @param tag
 * @param message
 */
@Serializable

data class LogEntry(

    @Contextual @SerialName(value = "timestamp")
    val timestamp: java.time.OffsetDateTime,

    @SerialName(value = "level")
    val level: LogEntry.Level,

    @SerialName(value = "tag")
    val tag: kotlin.String,

    @SerialName(value = "message")
    val message: kotlin.String,

    ) {

    /**
     *
     *
     * Values: DEBUG,INFO,WARN,ERROR
     */
    @Serializable
    enum class Level(val value: kotlin.String) {
        @SerialName(value = "DEBUG")
        DEBUG("DEBUG"),
        @SerialName(value = "INFO")
        INFO("INFO"),
        @SerialName(value = "WARN")
        WARN("WARN"),
        @SerialName(value = "ERROR")
        ERROR("ERROR");
    }

}

