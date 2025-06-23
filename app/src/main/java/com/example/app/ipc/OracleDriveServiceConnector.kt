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
         * Handles actions upon successful connection to the AuraDriveService.
         *
         * Initializes the service interface, updates connection state, registers a callback to receive service events, retrieves the service version, and notifies listeners of the connection event. If a RemoteException occurs during setup, resets the connection state and reports the error via the event callback.
         */
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            try {
                auraDriveService = IAuraDriveService.Stub.asInterface(service)
                _isServiceConnected.value = true

                // Register callback with all required methods
                serviceCallback = object : IAuraDriveCallback.Stub() {
                    // Connection events
                    override fun onConnected() {
                        onServiceEvent?.invoke(EVENT_CONNECTED, "Connected to service")
                    }

                    override fun onDisconnected(reason: String) {
                        onServiceEvent?.invoke(EVENT_DISCONNECTED, "Disconnected: $reason")
                    }

                    // Status updates
                    override fun onStatusUpdate(status: String) {
                        onServiceEvent?.invoke(EVENT_DATA_RECEIVED, "Status: $status")
                    }

                    override fun onError(errorCode: Int, errorMessage: String) {
                        onServiceEvent?.invoke(EVENT_ERROR, "Error $errorCode: $errorMessage")
                    }

                    // Data events
                    override fun onDataReceived(dataType: String, data: ByteArray) {
                        onServiceEvent?.invoke(
                            EVENT_DATA_RECEIVED,
                            "Data received - Type: $dataType, Size: ${data.size} bytes"
                        )
                    }

                    override fun onEvent(eventType: Int, eventData: String) {
                        onServiceEvent?.invoke(eventType, eventData)
                    }

                    // Module management
                    override fun onModuleStateChanged(packageName: String, enabled: Boolean) {
                        onServiceEvent?.invoke(
                            EVENT_DATA_RECEIVED,
                            "Module $packageName ${if (enabled) "enabled" else "disabled"}"
                        )
                    }

                    // System events
                    override fun onSystemEvent(eventType: Int, eventData: String) {
                        onServiceEvent?.invoke(
                            EVENT_DATA_RECEIVED,
                            "System event $eventType: $eventData"
                        )
                    }

                    // Deprecated method for backward compatibility
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
         * Handles the event when the service is disconnected.
         *
         * Unregisters the service callback, cleans up service references and state, and notifies listeners of the disconnection event.
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
         * Handles the event when the service binding dies unexpectedly.
         *
         * Cleans up the service connection and notifies the event callback with an error event.
         */
        override fun onBindingDied(name: ComponentName?) {
            cleanupService()
            onServiceEvent?.invoke(EVENT_ERROR, "Binding died")
        }

        /**
         * Handles the event when the service binding returns null.
         *
         * Cleans up the service connection and notifies the event callback with an error.
         */
        override fun onNullBinding(name: ComponentName?) {
            cleanupService()
            onServiceEvent?.invoke(EVENT_ERROR, "Received null binding")
        }
    }

    /**
     * Resets the service interface, callback, connection status, and service version to their default states.
     */
    private fun cleanupService() {
        auraDriveService = null
        serviceCallback = null
        _isServiceConnected.value = false
        _serviceVersion.value = null
    }

    /**
     * Attempts to bind to the AuraDriveService.
     *
     * @return `true` if the binding process was initiated, or `false` if already connected.
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
     * Unbinds from the AuraDriveService and cleans up service state.
     *
     * If connected, this method unregisters the service callback, unbinds from the service, and resets internal state. Errors during unbinding are reported via the event callback if provided.
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
     * Executes a command on the connected AuraDriveService asynchronously.
     *
     * @param command The command string to execute on the service.
     * @param params Optional parameters to include with the command.
     * @return A [Result] containing the command's output string on success, or an exception on failure.
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
     * Retrieves the current status string from the connected AuraDriveService.
     *
     * @return The status string if available, or null if the service is not connected or an error occurs.
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
         * Enables or disables an LSPosed module via the AuraDriveService.
         *
         * @param packageName The package name of the LSPosed module to toggle.
         * @param enable True to enable the module, false to disable it.
         * @return The result string from the service, or null if the operation fails.
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
     * Retrieves the detailed internal status from the AuraDriveService.
     *
     * @return The detailed internal status string, or null if the service is unavailable or an error occurs.
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
     * Retrieves the internal diagnostics log from the AuraDriveService.
     *
     * @return The diagnostics log as a string, or null if retrieval fails.
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
