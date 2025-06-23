package dev.aurakai.auraframefx.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OracleDriveControlViewModel @Inject constructor() : ViewModel() {
    // State Flows
    private val _isServiceConnected = MutableStateFlow(false)
    val isServiceConnected: StateFlow<Boolean> = _isServiceConnected.asStateFlow()

    private val _status = MutableStateFlow("Not connected")
    val status: StateFlow<String> = _status.asStateFlow()

    private val _detailedStatus = MutableStateFlow("")
    val detailedStatus: StateFlow<String> = _detailedStatus.asStateFlow()

    private val _diagnosticsLog = MutableStateFlow("")
    val diagnosticsLog: StateFlow<String> = _diagnosticsLog.asStateFlow()

    /**
     * Establishes a connection to the service and updates the connection status and logs.
     *
     * Sets the service connection state to connected, updates status messages, and appends a log entry indicating the service has been bound.
     */
    fun bindService() {
        viewModelScope.launch {
            _isServiceConnected.value = true
            _status.value = "Connected"
            _detailedStatus.value = "Service bound successfully"
            appendToLog("Service bound")
        }
    }

    /**
     * Updates the state to reflect that the service has been unbound.
     *
     * Sets the service connection status to disconnected, updates status messages, and appends an unbound event to the diagnostics log.
     */
    fun unbindService() {
        viewModelScope.launch {
            _isServiceConnected.value = false
            _status.value = "Disconnected"
            _detailedStatus.value = "Service unbound"
            appendToLog("Service unbound")
        }
    }

    /**
     * Refreshes the current status, updating status messages and appending a log entry.
     *
     * Simulates a refresh operation with a delay, then updates the status, detailed status with the current time, and logs the refresh event.
     */
    fun refreshStatus() {
        viewModelScope.launch {
            _status.value = "Refreshing..."
            // Simulate network/database call
            kotlinx.coroutines.delay(500)
            _status.value = "Ready"
            _detailedStatus.value = "Last updated: ${java.text.SimpleDateFormat("HH:mm:ss").format(java.util.Date())}"
            appendToLog("Status refreshed")
        }
    }

    /**
     * Enables or disables the specified package and updates the status and diagnostics log.
     *
     * Simulates the operation with a delay, then updates the status message to reflect the action taken.
     *
     * @param packageName The name of the package to enable or disable.
     * @param enable If true, the package is enabled; if false, it is disabled.
     */
    fun toggleForPackage(packageName: String, enable: Boolean) {
        viewModelScope.launch {
            val action = if (enable) "Enabling" else "Disabling"
            _status.value = "$action $packageName..."
            // Simulate network/database call
            kotlinx.coroutines.delay(1000)
            _status.value = if (enable) "Enabled $packageName" else "Disabled $packageName"
            appendToLog("$action $packageName completed")
        }
    }

    /**
     * Appends a timestamped message to the diagnostics log.
     *
     * @param message The message to append to the log.
     */
    private fun appendToLog(message: String) {
        _diagnosticsLog.value += "[${java.text.SimpleDateFormat("HH:mm:ss").format(java.util.Date())}] $message\n"
    }
}
