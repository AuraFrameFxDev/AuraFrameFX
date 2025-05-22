package dev.aurakai.auraframefx.service

import android.app.Service
import android.content.Intent
import android.os.IBinder

class XposedBridgeService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null
}
