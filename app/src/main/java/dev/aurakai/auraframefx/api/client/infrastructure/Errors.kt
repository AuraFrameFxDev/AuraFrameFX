@file:Suppress("unused")
package dev.aurakai.auraframefx.api.client.infrastructure

import java.lang.RuntimeException

open class ClientException(message: kotlin.String? = null, val statusCode: Int = -1, val response: Response? = null) : RuntimeException(message) {

    public companion object {
        private const val serialVersionUID: Long = 123L
    }
}

open class ServerException(message: kotlin.String? = null, val statusCode: Int = -1, val response: Response? = null) : RuntimeException(message) {

    public companion object {
        private const val serialVersionUID: Long = 456L
    }
}
