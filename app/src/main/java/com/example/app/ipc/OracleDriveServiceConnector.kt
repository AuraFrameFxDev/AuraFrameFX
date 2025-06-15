package com.example.app.ipc

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

/**
 * Connector class for communicating with the AuraDriveService.
 * Handles service binding, unbinding, and provides methods to interact with the service.
 */
class OracleDriveServiceConnector(
    private val context: Context,
    private val onServiceEvent: ((eventType: Int, message: String) -> Unit)? = null,
) {
    private var auraDriveService: IAuraDriveService? = null
    private var serviceCallback: IAuraDriveCallback? = null
    private val _isServiceConnected = MutableStateFlow(false)
    private val _serviceVersion = MutableStateFlow<String?>(null)

    val isServiceConnected: StateFlow<Boolean> = _isServiceConnected.asStateFlow()
    val serviceVersion: StateFlow<String?> = _serviceVersion.asStateFlow()

    private val serviceConnection = object : ServiceConnection {
        /**
         * Initializes the connection to the AuraDriveService when the service is connected.
         *
         * Sets up the service interface, registers a callback to receive service events, updates connection state, retrieves the service version, and notifies listeners of the connection status. If a RemoteException occurs during setup, resets the connection state and reports the error via the event callback.
         */
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            try {
                auraDriveService = IAuraDriveService.Stub.asInterface(service)
                _isServiceConnected.value = true

                // Register callback with all required methods
                serviceCallback = object : IAuraDriveCallback.Stub() {
                    /**
                     * Invokes the service event callback to indicate a successful connection to the service.
                     */
                    override fun onConnected() {
                        onServiceEvent?.invoke(EVENT_CONNECTED, "Connected to service")
                    }

                    /**
                     * Handles service disconnection events and notifies the registered event callback with the reason.
                     *
                     * @param reason The reason for the service disconnection.
                     */
                    override fun onDisconnected(reason: String) {
                        onServiceEvent?.invoke(EVENT_DISCONNECTED, "Disconnected: $reason")
                    }

                    /**
                     * Handles status update events from the service and notifies the registered event callback.
                     *
                     * @param status The updated status string received from the service.
                     */
                    override fun onStatusUpdate(status: String) {
                        onServiceEvent?.invoke(EVENT_DATA_RECEIVED, "Status: $status")
                    }

                    /**
                     * Handles error events from the service and invokes the service event callback with the error details.
                     *
                     * @param errorCode The error code reported by the service.
                     * @param errorMessage The error message associated with the error code.
                     */
                    override fun onError(errorCode: Int, errorMessage: String) {
                        onServiceEvent?.invoke(EVENT_ERROR, "Error $errorCode: $errorMessage")
                    }

                    /**
                     * Handles data received events from the service and notifies the registered event callback with the data type and size.
                     *
                     * @param dataType The type of data received.
                     * @param data The data payload as a byte array.
                     */
                    override fun onDataReceived(dataType: String, data: ByteArray) {
                        onServiceEvent?.invoke(
                            EVENT_DATA_RECEIVED,
                            "Data received - Type: $dataType, Size: ${data.size} bytes"
                        )
                    }

                    /**
                     * Handles a service event by invoking the registered event callback with the event type and data.
                     *
                     * @param eventType The type of event received from the service.
                     * @param eventData The associated data or message for the event.
                     */
                    override fun onEvent(eventType: Int, eventData: String) {
                        onServiceEvent?.invoke(eventType, eventData)
                    }

                    /**
                     * Handles module state changes by notifying the service event callback with the updated state.
                     *
                     * @param packageName The package name of the module whose state has changed.
                     * @param enabled True if the module is enabled, false if disabled.
                     */
                    override fun onModuleStateChanged(packageName: String, enabled: Boolean) {
                        onServiceEvent?.invoke(
                            EVENT_DATA_RECEIVED,
                            "Module $packageName ${if (enabled) "enabled" else "disabled"}"
                        )
                    }

                    /**
                     * Handles system events received from the remote service and notifies the registered event callback.
                     *
                     * @param eventType The type of the system event.
                     * @param eventData Additional data associated with the event.
                     */
                    override fun onSystemEvent(eventType: Int, eventData: String) {
                        onServiceEvent?.invoke(
                            EVENT_DATA_RECEIVED,
                            "System event $eventType: $eventData"
                        )
                    }

                    /**
                     * Handles deprecated service events for backward compatibility by invoking the provided event callback.
                     *
                     * @param eventType The type of the service event.
                     * @param message The message associated with the event.
                     */
                    override fun onServiceEvent(eventType: Int, message: String) {
                        onServiceEvent?.invoke(eventType, message)
                    }
                }

                auraDriveService?.registerCallback(serviceCallback)

                // Get service version
                _serviceVersion.value = auraDriveService?.serviceVersion
                onServiceEvent?.invoke(EVENT_CONNECTED, "Service connected")
            } catch (e: RemoteException) {
                _isServiceConnected.value = false
                onServiceEvent?.invoke(EVENT_ERROR, "Service connection error: ${e.message}")
            }
        }

        /**
         * Invoked when the service connection is lost.
         *
         * Unregisters the service callback if present, resets internal state, and notifies listeners of the disconnection event.
         */
        override fun onServiceDisconnected(name: ComponentName?) {
            try {
                serviceCallback?.let { callback ->
                    auraDriveService?.unregisterCallback(callback)
                }
            } catch (e: RemoteException) {
                // Ignore
            }

            cleanupService()
            onServiceEvent?.invoke(EVENT_DISCONNECTED, "Service disconnected")
        }

        /**
         * Invoked when the service binding dies unexpectedly.
         *
         * Cleans up the service connection and triggers the error event callback.
         */
        override fun onBindingDied(name: ComponentName?) {
            cleanupService()
            onServiceEvent?.invoke(EVENT_ERROR, "Binding died")
        }

        /**
         * Invoked when the service binding returns null, indicating the service is unavailable.
         *
         * Resets internal state and notifies the event callback with an error event.
         */
        override fun onNullBinding(name: ComponentName?) {
            cleanupService()
            onServiceEvent?.invoke(EVENT_ERROR, "Received null binding")
        }
    }

    /**
     * Clears internal references to the service and resets connection state.
     *
     * Resets the service interface, callback, connection status, and service version to their default values.
     */
    private fun cleanupService() {
        auraDriveService = null
        serviceCallback = null
        _isServiceConnected.value = false
        _serviceVersion.value = null
    }

    /**
     * Initiates binding to the AuraDriveService if not already connected.
     *
     * @return `true` if binding was started, or `false` if the service is already connected or if binding fails.
     *
     * If binding fails due to an exception, the internal state is reset and an error event is reported through the event callback.
     */
    fun bindService(): Boolean {
        if (_isServiceConnected.value) return false

        val intent = Intent().apply {
            component = ComponentName(
                "com.genesis.ai.app",
                "com.genesis.ai.app.service.AuraDriveServiceImpl"
            )
        }

        return try {
            context.bindService(
                intent,
                serviceConnection,
                Context.BIND_AUTO_CREATE or Context.BIND_IMPORTANT
            )
            true
        } catch (e: SecurityException) {
            cleanupService()
            onServiceEvent?.invoke(EVENT_ERROR, "Security exception: ${e.message}")
            false
        } catch (e: Exception) {
            cleanupService()
            onServiceEvent?.invoke(EVENT_ERROR, "Failed to bind service: ${e.message}")
            false
        }
    }

    /**
     * Unbinds from the AuraDriveService and resets the connector state.
     *
     * If currently connected, attempts to unregister the service callback and unbind from the service. Cleans up all internal references and state regardless of outcome.
     */
    fun unbindService() {
        if (!_isServiceConnected.value) return

        try {
            serviceCallback?.let { callback ->
                auraDriveService?.unregisterCallback(callback)
            }
            context.unbindService(serviceConnection)
        } catch (e: Exception) {
            onServiceEvent?.invoke(EVENT_ERROR, "Error during unbind: ${e.message}")
        } finally {
            cleanupService()
        }
    }

    /**
     * Sends a command with optional parameters to the connected AuraDriveService and returns the result.
     *
     * @param command The command to execute on the service.
     * @param params Optional parameters to include with the command.
     * @return A [Result] containing the service's response string on success, or an exception if the command fails or the service is not connected.
     */
    suspend fun executeCommand(
        command: String,
        params: Map<String, Any> = emptyMap(),
    ): Result<String> {
        return if (!_isServiceConnected.value) {
            Result.failure(IllegalStateException("Service not connected"))
        } else {
            try {
                val result = withContext(Dispatchers.IO) {
                    auraDriveService?.executeCommand(command, params)
                        ?: throw RemoteException("Service returned null")
                }
                Result.success(result)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Retrieves the current status string from the connected Oracle Drive service.
     *
     * @return The status string if available, or null if the service is unavailable or the request fails.
     */
    suspend fun getStatusFromOracleDrive(): String? = withContext(Dispatchers.IO) {
        try {
            auraDriveService?.getOracleDriveStatus()
        } catch (e: RemoteException) {
            onServiceEvent?.invoke(EVENT_ERROR, "Failed to get status: ${e.message}")
            null
        }
    }

    /**
         * Toggles the enabled state of an LSPosed module on the connected AuraDriveService.
         *
         * @param packageName The package name of the module to enable or disable.
         * @param enable Whether to enable (true) or disable (false) the module.
         * @return The result string from the service, or null if the operation fails or a remote exception occurs.
         */
    suspend fun toggleModuleOnOracleDrive(packageName: String, enable: Boolean): String? =
        withContext(Dispatchers.IO) {
            try {
                auraDriveService?.toggleLSPosedModule(packageName, enable)
            } catch (e: RemoteException) {
                onServiceEvent?.invoke(EVENT_ERROR, "Failed to toggle module: ${e.message}")
                null
            }
        }

    /**
     * Retrieves a detailed internal status report from the connected AuraDriveService.
     *
     * @return The detailed internal status as a string, or null if the service is unavailable or the request fails.
     */
    suspend fun getDetailedInternalStatus(): String? = withContext(Dispatchers.IO) {
        try {
            auraDriveService?.getDetailedInternalStatus()
        } catch (e: RemoteException) {
            onServiceEvent?.invoke(EVENT_ERROR, "Failed to get detailed status: ${e.message}")
            null
        }
    }

    /**
     * Retrieves the internal diagnostics log from the connected AuraDriveService.
     *
     * @return The diagnostics log as a string, or null if the service is unavailable or an error occurs.
     */
    suspend fun getInternalDiagnosticsLog(): String? = withContext(Dispatchers.IO) {
        try {
            auraDriveService?.getInternalDiagnosticsLog()
        } catch (e: RemoteException) {
            onServiceEvent?.invoke(EVENT_ERROR, "Failed to get diagnostics: ${e.message}")
            null
        }
    }

    companion object {
        // Event types
        const val EVENT_CONNECTED = 1
        const val EVENT_DISCONNECTED = 2
        const val EVENT_ERROR = 3
        const val EVENT_DATA_RECEIVED = 4
    }
}
