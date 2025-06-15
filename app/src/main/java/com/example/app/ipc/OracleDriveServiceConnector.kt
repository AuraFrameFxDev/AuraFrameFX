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

        override fun onBindingDied(name: ComponentName?) {
            cleanupService()
            onServiceEvent?.invoke(EVENT_ERROR, "Binding died")
        }

        override fun onNullBinding(name: ComponentName?) {
            cleanupService()
            onServiceEvent?.invoke(EVENT_ERROR, "Received null binding")
        }
    }

    private fun cleanupService() {
        auraDriveService = null
        serviceCallback = null
        _isServiceConnected.value = false
        _serviceVersion.value = null
    }

    /**
     * Binds to the AuraDriveService
     * @return true if binding was attempted, false if already bound
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
     * Unbinds from the AuraDriveService
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
     * Execute a command on the service
     * @param command The command to execute
     * @param params Optional parameters for the command
     * @return Result of the command execution
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
     * Get the current status from Oracle Drive
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
     * Toggle an LSPosed module on/off
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
     * Get detailed internal status from the service
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
     * Get internal diagnostics log from the service
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
