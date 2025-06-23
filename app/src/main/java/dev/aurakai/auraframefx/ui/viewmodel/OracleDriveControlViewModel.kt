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

    // Service binding methods
    fun bindService() {
        viewModelScope.launch {
            _isServiceConnected.value = true
            _status.value = "Connected"
            _detailedStatus.value = "Service bound successfully"
            appendToLog("Service bound")
        }
    }

    fun unbindService() {
        viewModelScope.launch {
            _isServiceConnected.value = false
            _status.value = "Disconnected"
            _detailedStatus.value = "Service unbound"
            appendToLog("Service unbound")
        }
    }

    // Status refresh
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

    // Package control
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

    // Helper method to append to log
    private fun appendToLog(message: String) {
        _diagnosticsLog.value += "[${java.text.SimpleDateFormat("HH:mm:ss").format(java.util.Date())}] $message\n"
    }
}
