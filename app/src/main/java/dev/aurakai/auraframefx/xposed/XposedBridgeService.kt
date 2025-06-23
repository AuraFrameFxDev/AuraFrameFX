package dev.aurakai.auraframefx.xposed

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class XposedBridgeService @Inject constructor(
    // Example dependency injection (add real dependencies as needed)
    // private val someDependency: SomeType
) : Service() {
    private val tag = "XposedBridgeService"

    override fun onCreate() {
        super.onCreate()
        Log.d(tag, "XposedBridgeService created.")
        // Initialization logic for the service
        // e.g., setup communication channels, start background tasks, etc.
    }

    override fun onBind(_intent: Intent?): IBinder? {
        Log.d(tag, "onBind called, returning null.")
        // Implement binding if needed for Xposed IPC
        return null
    }

    override fun onStartCommand(_intent: Intent?, _flags: Int, _startId: Int): Int {
        Log.d(tag, "onStartCommand called.")
        // Handle commands sent to this service
        // Use _intent, _flags, _startId as needed
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(tag, "XposedBridgeService destroyed.")
        // Cleanup logic, e.g., close resources, stop tasks
    }
}
