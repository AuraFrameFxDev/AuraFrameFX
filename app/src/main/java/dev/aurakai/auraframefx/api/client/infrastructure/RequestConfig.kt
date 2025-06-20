package dev.aurakai.auraframefx.api.client.infrastructure

/**
 * Defines a config object for a given request.
 * NOTE: This object doesn't include 'body' because it
 *       allows for caching of the constructed object
 *       for many request definitions.
 * NOTE: Headers is a Map<String,String> because rfc2616 defines
 *       multi-valued headers as csv-only.
 */
public data class RequestConfig<T>(
    public val method: RequestMethod,
    public val path: String,
    public val headers: MutableMap<String, String> = mutableMapOf(),
    public val params: MutableMap<String, Any> = mutableMapOf(),
    public val query: MutableMap<String, List<String>> = mutableMapOf(),
    public val requiresAuthentication: Boolean,
    public val body: T? = null
)
