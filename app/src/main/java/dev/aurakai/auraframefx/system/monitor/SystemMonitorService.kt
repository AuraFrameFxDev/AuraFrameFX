package dev.aurakai.auraframefx.system.monitor

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.BatteryManager
import android.os.IBinder
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import dev.aurakai.auraframefx.ai.services.KaiAIService
import dev.aurakai.auraframefx.data.logging.AuraFxLogger
import dev.aurakai.auraframefx.data.offline.OfflineDataManager
import dev.aurakai.auraframefx.model.requests.AiRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SystemMonitorService : Service() {

    @Inject
    lateinit var kaiService: KaiAIService

    @Inject
    lateinit var offlineDataManager: OfflineDataManager

    @Inject
    lateinit var auraFxLogger: AuraFxLogger // NEW
    // @Inject lateinit var auraAIService: AuraAIService // Not used in current snippet

    private val TAG = "SystemMonitorService"
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val batteryReceiver = object : BroadcastReceiver() {
        /**
         * Handles battery status change broadcasts and triggers a low battery alert if conditions are met.
         *
         * When a battery status change is received, checks if system monitoring and battery optimization are enabled,
         * and if the battery percentage is at or below the configured low threshold while not charging. If so, sends
         * a "battery_alert" to the AI service with the current battery level and charging status.
         */
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_BATTERY_CHANGED) {
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)

                if (level == -1 || scale == -1) {
                    auraFxLogger.w(TAG, "Could not get valid battery level/scale.")
                    return
                }
                val batteryPct = level / scale.toFloat() * 100
                val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                        status == BatteryManager.BATTERY_STATUS_FULL

                serviceScope.launch {
                    try {
                        val config = offlineDataManager.loadCriticalOfflineData()?.systemMonitoring
                        // Check existing conditions, and ensure isCharging is false for low battery action
                        if (config?.enabled == true && config.batteryOptimizationEnabled &&
                            batteryPct <= config.batteryThresholdLow && !isCharging
                        ) {
                            auraFxLogger.w(
                                TAG,
                                "Battery low alert! Current level: ${batteryPct.toInt()}% (Threshold: ${config.batteryThresholdLow}%), Charging: $isCharging"
                            )
                            kaiService.processSystemAlert(
                                "battery_alert",
                                mapOf(
                                    "level" to batteryPct.toInt().toString(),
                                    "isCharging" to isCharging.toString()
                                )
                            ).firstOrNull()?.let { message ->
                                Log.d(TAG, "Kai's response to battery_alert: ${message.content}")
                                // TODO: Propagate this to UI
                            }
                        } else {
                            Log.d(
                                TAG,
                                "Battery status: ${batteryPct.toInt()}%, Charging: $isCharging. Conditions for alert not met or feature disabled."
                            )
                        }
                    } catch (e: Exception) {
                        auraFxLogger.e(TAG, "Error processing battery level in serviceScope", e)
                    }
                }
            }
        }
    }

    private val networkReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ConnectivityManager.CONNECTIVITY_ACTION) {
                val connectivityMgr =
                    context?.getSystemService(CONNECTIVITY_SERVICE) as? ConnectivityManager

                @Suppress("DEPRECATION") // activeNetworkInfo is deprecated but needed for simpler overall connected check here
                val activeNetworkInfo = connectivityMgr?.activeNetworkInfo

                @Suppress("DEPRECATION")
                val isConnected = activeNetworkInfo != null && activeNetworkInfo.isConnected

                serviceScope.launch {
                    if (!isConnected) {
                        Log.i(
                            TAG,
                            "Network change detected: Disconnected. Reporting 'network_offline_alert' to Kai."
                        )
                        kaiService.processSystemAlert(
                            "network_offline_alert",
                            mapOf("status" to "disconnected")
                        ).firstOrNull()?.let { message ->
                            Log.d(TAG, "Kai's network_offline_alert response: ${message.content}")
                        }
                    } else {
                        Log.i(
                            TAG,
                            "Network change detected: Connected. Reporting 'network_online_alert' to Kai."
                        )
                        kaiService.processSystemAlert(
                            "network_online_alert",
                            mapOf("status" to "connected")
                        ).firstOrNull()?.let { message ->
                            Log.d(TAG, "Kai's network_online_alert response: ${message.content}")
                        }
                    }
                }
            }
        }
    }

    /**
     * Initializes the service by registering battery and network status receivers and starting periodic security scans.
     *
     * Registers broadcast receivers to monitor battery and network changes. Launches a coroutine that periodically checks the system monitoring configuration and, if enabled, triggers security scans at the configured interval. Handles errors during receiver registration and scan execution using the injected logger.
     */
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "SystemMonitorService created.")

        try {
            val batteryFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            registerReceiver(batteryReceiver, batteryFilter)
            Log.d(TAG, "BatteryReceiver registered.")

            val networkFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            registerReceiver(networkReceiver, networkFilter)
            Log.d(TAG, "NetworkReceiver registered.")
        } catch (e: Exception) {
            auraFxLogger.e(TAG, "Error registering receivers: ${e.message}", e)
        }

        serviceScope.launch {
            // Initial load of config to prevent multiple loads in the loop if not changing
            var currentMonitoringConfig =
                offlineDataManager.loadCriticalOfflineData()?.systemMonitoring
            Log.d(TAG, "Initial systemMonitoringConfig: $currentMonitoringConfig")

            while (true) {
                // Re-fetch config periodically or rely on a mechanism that updates it if it can change at runtime
                // For now, using the initially loaded one or re-loading if it was null.
                if (currentMonitoringConfig == null) {
                    currentMonitoringConfig =
                        offlineDataManager.loadCriticalOfflineData()?.systemMonitoring
                    Log.d(TAG, "Re-fetched systemMonitoringConfig: $currentMonitoringConfig")
                }

                if (currentMonitoringConfig?.enabled == true && currentMonitoringConfig.securityScanIntervalHours > 0) {
                    val intervalMillis =
                        currentMonitoringConfig.securityScanIntervalHours * 60 * 60 * 1000L
                    // val intervalMillis = currentMonitoringConfig.securityScanIntervalHours * 10 * 1000L // Shorter for testing
                    Log.d(
                        TAG,
                        "Next security scan in approx ${currentMonitoringConfig.securityScanIntervalHours} hours (or test equivalent). Waiting for $intervalMillis ms."
                    )

                    delay(intervalMillis)

                    Log.i(TAG, "Initiating periodic security scan.")
                    try {
                        kaiService.processRequest(
                            AiRequest(
                                "Perform a routine system security scan.",
                                "security_scan"
                            )
                        ).firstOrNull()?.let { message ->
                            Log.d(TAG, "Kai's periodic security scan response: ${message.content}")
                            // TODO: Propagate results (e.g., notification if issues found)
                        }
                    } catch (e: Exception) {
                        auraFxLogger.e(
                            TAG,
                            "Error during periodic security scan request to KaiService",
                            e
                        )
                    }
                    // After a scan, it might be good to re-fetch config in case interval changed.
                    currentMonitoringConfig =
                        offlineDataManager.loadCriticalOfflineData()?.systemMonitoring
                } else {
                    val disabledCheckInterval = 60 * 60 * 1000L // 1 hour
                    Log.d(
                        TAG,
                        "Periodic security scan disabled or interval is 0. Re-checking config in $disabledCheckInterval ms."
                    )
                    delay(disabledCheckInterval)
                    currentMonitoringConfig =
                        offlineDataManager.loadCriticalOfflineData()?.systemMonitoring // Re-fetch config
                }
            }
        }
    }

    /**
     * Handles the service start command by initiating loading of critical offline data.
     *
     * Ensures that essential data is available for system monitoring operations. Returns `START_STICKY` to keep the service running.
     *
     * @return The flag indicating the service should remain running (`START_STICKY`).
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "SystemMonitorService received start command.")
        serviceScope.launch {
            try {
                offlineDataManager.loadCriticalOfflineData() // Ensure data is loaded if not already
            } catch (e: Exception) {
                auraFxLogger.e(TAG, "Error loading critical data in onStartCommand", e)
            }
        }
        return START_STICKY
    }

    /**
     * Cleans up resources when the service is destroyed.
     *
     * Unregisters battery and network receivers, cancels the service coroutine scope, and logs any errors encountered during cleanup.
     */
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "SystemMonitorService destroyed.")
        try {
            unregisterReceiver(batteryReceiver)
            Log.d(TAG, "BatteryReceiver unregistered.")
            unregisterReceiver(networkReceiver)
            Log.d(TAG, "NetworkReceiver unregistered.")
        } catch (e: Exception) {
            auraFxLogger.e(TAG, "Error unregistering receivers: ${e.message}", e)
        }
        serviceScope.cancel()
        Log.d(TAG, "Service scope cancelled.")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
