package dev.aurakai.auraframefx.ai

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Process
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

/**
 * Class to hold security-related information that can be exchanged
 * between Neural Whisper (Aura) and Kai
 */
@Singleton
class SecurityContext @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val errorCounter = AtomicInteger(0)
    private val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
    private val activityManager =
        context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

    // System metrics collection
    private var lastCpuTime: Long = 0
    private var lastAppCpuTime: Long = 0

    data class SecurityMetrics(
        val adBlockingActive: Boolean = false,
        val ramUsage: Double = 0.0,
        val cpuUsage: Double = 0.0,
        val batteryTemp: Double = 0.0,
        val recentErrors: Int = 0,
        val isDeviceSecure: Boolean = false,
        val isBatteryCharging: Boolean = false,
        val batteryLevel: Int = 0,
        val isOverheating: Boolean = false,
        val isLowMemory: Boolean = false,
        val isRooted: Boolean = false,
    )

    /**
     * Get current security metrics
     */
    suspend fun getCurrentMetrics(): SecurityMetrics = withContext(Dispatchers.IO) {
        SecurityMetrics(
            adBlockingActive = isAdBlockingActive(),
            ramUsage = getCurrentRamUsage(),
            cpuUsage = getCurrentCpuUsage(),
            batteryTemp = getBatteryTemperature(),
            recentErrors = errorCounter.get(),
            isDeviceSecure = isDeviceSecure(),
            isBatteryCharging = isBatteryCharging(),
            batteryLevel = getBatteryLevel(),
            isOverheating = isDeviceOverheating(),
            isLowMemory = isLowMemory(),
            isRooted = isDeviceRooted()
        )
    }

    /**
     * Observe security metrics as a flow
     */
    fun observeMetrics(interval: Long = 5000): Flow<SecurityMetrics> = flow {
        while (true) {
            emit(getCurrentMetrics())
            kotlinx.coroutines.delay(interval)
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Record a security-related error
     */
    fun recordError() {
        errorCounter.incrementAndGet()
    }

    /**
     * Reset error counter
     */
    fun resetErrorCounter() {
        errorCounter.set(0)
    }

    private fun isAdBlockingActive(): Boolean {
        // Check if any ad-blocking apps are installed
        val adBlockingPackages = listOf(
            "org.adaway",
            "com.bigtincan.android.adfree",
            "com.avast.android.adblock"
        )

        return adBlockingPackages.any { isPackageInstalled(it) }
    }

    private fun getCurrentRamUsage(): Double {
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)

        val totalMemory = memoryInfo.totalMem.toDouble()
        val usedMemory = totalMemory - memoryInfo.availMem

        return (usedMemory / totalMemory * 100).roundToInt() / 100.0
    }

    private fun getCurrentCpuUsage(): Double {
        return try {
            val pid = Process.myPid()
            val statFile = File("/proc/$pid/stat")

            if (!statFile.exists()) return 0.0

            val reader = BufferedReader(FileReader(statFile))
            val stats = reader.use { it.readLine().split("\\s+".toRegex()) }

            val utime = stats[13].toLong()
            val stime = stats[14].toLong()
            val cutime = stats[15].toLong()
            val cstime = stats[16].toLong()

            val totalCpuTime = utime + stime + cutime + cstime

            // Calculate CPU usage percentage
            val cpuUsage = if (lastCpuTime > 0) {
                val totalDiff = totalCpuTime - lastAppCpuTime
                val cpuDiff = (Runtime.getRuntime().availableProcessors() * 100).toLong()
                (totalDiff * 100 / cpuDiff).toDouble()
            } else {
                0.0
            }

            lastCpuTime = System.currentTimeMillis()
            lastAppCpuTime = totalCpuTime

            cpuUsage.coerceIn(0.0, 100.0)
        } catch (e: Exception) {
            e.printStackTrace()
            0.0
        }
    }

    private fun getBatteryTemperature(): Double {
        return try {
            val batteryIntent =
                context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            val temp = batteryIntent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) ?: 0
            (temp / 10.0).roundToInt() / 10.0 // Convert to Celsius with one decimal place
        } catch (e: Exception) {
            e.printStackTrace()
            0.0
        }
    }

    private fun isDeviceSecure(): Boolean {
        val keyguardManager =
            ContextCompat.getSystemService(context, android.app.KeyguardManager::class.java)
        return keyguardManager?.isDeviceSecure == true
    }

    private fun isBatteryCharging(): Boolean {
        val batteryStatus =
            context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val status = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        return status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL
    }

    private fun getBatteryLevel(): Int {
        val batteryIntent =
            context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        return batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
    }

    private fun isDeviceOverheating(): Boolean {
        // Consider device overheating if battery temperature is above 40°C
        return getBatteryTemperature() > 40.0
    }

    private fun isLowMemory(): Boolean {
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        return memoryInfo.lowMemory
    }

    private fun isDeviceRooted(): Boolean {
        // Check for root binaries
        val paths = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su"
        )

        return paths.any { File(it).exists() } || isRootedWithBusyBox()
    }

    private fun isRootedWithBusyBox(): Boolean {
        // Check for busybox binary
        val paths = System.getenv("PATH")?.split(":") ?: emptyList()
        return paths.any { path ->
            File("$path/busybox").exists()
        }
    }

    private fun isPackageInstalled(packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Determine if there are any active security concerns
     */
    fun hasSecurityConcerns(): Boolean {
        return ramUsage > 80.0 || cpuUsage > 85.0 || batteryTemp > 40.0 || recentErrors > 0
    }

    /**
     * Get a human-readable description of security concerns
     */
    fun getSecurityConcernsDescription(): String {
        val concerns = mutableListOf<String>()

        if (ramUsage > 80.0) {
            concerns.add("High RAM usage (${ramUsage.toInt()}%)")
        }

        if (cpuUsage > 85.0) {
            concerns.add("High CPU usage (${cpuUsage.toInt()}%)")
        }

        if (batteryTemp > 40.0) {
            concerns.add("Elevated battery temperature (${batteryTemp.toInt()}°C)")
        }

        if (recentErrors > 0) {
            concerns.add("$recentErrors recent error events")
        }

        return if (concerns.isEmpty()) {
            "No security concerns detected"
        } else {
            "Security concerns: ${concerns.joinToString(", ")}"
        }
    }
}
