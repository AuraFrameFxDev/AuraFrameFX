package com.example.app

import android.app.Service
import android.content.Intent
import android.os.IBinder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class VertexSyncService @Inject constructor() : Service() {
    // TODO: If this service has dependencies to be injected, add them to the constructor.

    override fun onBind(intent: Intent?): IBinder? {
        // TODO: Parameter intent should be _intent if not used.
        return null // TODO: Implement if binding is needed
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // TODO: Implement service logic
        return START_NOT_STICKY
    }
}
