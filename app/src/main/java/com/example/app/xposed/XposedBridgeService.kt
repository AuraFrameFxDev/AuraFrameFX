package com.example.app.xposed

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class XposedBridgeService @Inject constructor() : Service() {
    // TODO: If this service has dependencies to be injected, add them to the constructor.
    // Note: Hilt DI might be complex for services primarily accessed by Xposed.
    // This ensures it can be a Hilt entry point if also used as a regular Android Service.

    private val tag = "XposedBridgeService"

    override fun onCreate() {
        super.onCreate()
        Log.d(tag, "XposedBridgeService created.")
        // TODO: Initialization logic for the service, if any.
        // This service might be used for communication between Xposed hooks and the app,
        // or for other background tasks initiated by Xposed modules.
    }

    override fun onBind(_intent: Intent?): IBinder? { // intent -> _intent
        // This service is not designed to be bound by typical app components.
        // Xposed services might use other IPC mechanisms or might not be bound at all.
        // TODO: Implement if binding is necessary for a specific Xposed inter-process communication.
        // TODO: Parameter _intent reported as unused.
        Log.d(tag, "onBind called, returning null.")
        return null
    }

    override fun onStartCommand(
        _intent: Intent?,
        _flags: Int,
        _startId: Int,
    ): Int { // params underscored
        Log.d(tag, "onStartCommand called.")
        // TODO: Handle commands sent to this service.
        // TODO: Utilize parameters if needed by actual implementation.
        return START_STICKY // Or START_NOT_STICKY depending on desired behavior
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(tag, "XposedBridgeService destroyed.")
        // TODO: Cleanup logic, if any.
    }
}
