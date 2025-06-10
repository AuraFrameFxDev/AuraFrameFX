package dev.aurakai.auraframefx.viewmodel

// Import for SimpleDateFormat and Date if not already covered by other viewmodel files
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.data.logging.AuraFxLogger
import dev.aurakai.auraframefx.data.network.CloudStatusMonitor
import dev.aurakai.auraframefx.data.offline.OfflineDataManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class DiagnosticsViewModel @Inject constructor(
    private val auraFxLogger: AuraFxLogger,
    private val cloudStatusMonitor: CloudStatusMonitor,
    private val offlineDataManager: OfflineDataManager,
) : ViewModel() {

    private val TAG = "DiagnosticsViewModel" // For potential Logcat logging from ViewModel itself

    private val _currentLogs = MutableStateFlow("Loading logs...")
    val currentLogs: StateFlow<String> = _currentLogs.asStateFlow()

    private val _systemStatus = MutableStateFlow<Map<String, String>>(emptyMap())
    val systemStatus: StateFlow<Map<String, String>> = _systemStatus.asStateFlow()

    init {
        // Collect real-time cloud status updates
        viewModelScope.launch {
            cloudStatusMonitor.isCloudReachable.collect { isReachable ->
                _systemStatus.update { currentMap ->
                    currentMap.toMutableMap().apply {
                        put(
                            "Cloud API Status",
                            if (isReachable) "Online" else "Offline (or Check Error)"
                        )
                    }
                }
            }
        }

        // Load initial system statuses and logs
        viewModelScope.launch {
            // Initial log load
            refreshLogs()

            // Load other statuses
            val offlineData = offlineDataManager.loadCriticalOfflineData() // Suspend call
            _systemStatus.update { currentMap ->
                currentMap.toMutableMap().apply {
                    put("Last Full Sync (Offline Data)", offlineData?.lastFullSyncTimestamp?.let {
                        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date(it))
                    } ?: "N/A")
                    put(
                        "Offline AI Config Version (Timestamp)",
                        (offlineData?.aiConfig?.lastSyncTimestamp ?: 0L).let {
                            if (it == 0L) "N/A" else SimpleDateFormat(
                                "yyyy-MM-dd HH:mm:ss",
                                Locale.US
                            ).format(Date(it))
                        })
                    put(
                        "Monitoring Enabled",
                        (offlineData?.systemMonitoring?.enabled ?: false).toString()
                    )
                    put(
                        "Contextual Memory Last Update",
                        offlineData?.contextualMemory?.lastUpdateTimestamp?.let {
                            if (it == 0L) "N/A" else SimpleDateFormat(
                                "yyyy-MM-dd HH:mm:ss",
                                Locale.US
                            ).format(Date(it))
                        } ?: "N/A")
                    // Add more status items as needed
                }
            }
        }

        // Periodically refresh logs (or provide a refresh button in UI)
        viewModelScope.launch {
            while (true) {
                delay(5000) // Refresh every 5 seconds
                refreshLogs()
            }
        }
    }

    /**
     * Asynchronously refreshes the current day's logs and updates the observable log state.
     *
     * If no logs are available, sets a placeholder message. In case of an error, updates the log state with an error message.
     */
    fun refreshLogs() {
        viewModelScope.launch {
            try {
                val logsContent = auraFxLogger.readCurrentDayLogs()
                _currentLogs.update { if (logsContent.isEmpty()) "No logs for today yet." else logsContent }
            } catch (e: Exception) {
                // Log error to Logcat if reading logs fails, and update UI
                Log.e(TAG, "Failed to refresh logs from AuraFxLogger", e)
                _currentLogs.update { "Error loading logs: ${e.message}" }
            }
        }
    }

    // TODO: Add methods to:
    // - Read all logs (not just current day)
    // - Filter logs by level or tag
    // - Clear logs (with confirmation)
    // - Trigger a manual cloud reachability check via cloudStatusMonitor.checkActualInternetReachability()
    // - Display more detailed config from offlineDataManager.loadCriticalOfflineData()
}
