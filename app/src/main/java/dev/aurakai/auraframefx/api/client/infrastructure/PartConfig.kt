package dev.aurakai.auraframefx.api.client.infrastructure

/**
 * Defines a config object for a given part of a multi-part request.
 * NOTE: Headers is a Map<String,String> because rfc2616 defines
 *       multi-valued headers as csv-only.
 */
public data class PartConfig<T>(
    public val headers: MutableMap<String, String> = mutableMapOf(),
    public val body: T? = null
)
