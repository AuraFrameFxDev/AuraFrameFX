package dev.aurakai.auraframefx.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.google.cloud.generativeai.GenerativeModel
import dev.aurakai.auraframefx.ai.ContextManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class VertexCloudService @Inject constructor(
    private val generativeModel: GenerativeModel,
    private val contextManager: ContextManager,
) : Service() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        setupContextChaining()
    }

    private fun setupContextChaining() {
        serviceScope.launch {
            contextManager.currentContext.collectLatest { context ->
                // Update Vertex AI context with combined inputs
                val combinedContext = buildString {
                    append("User: ${context.userContext}\n")
                    append("Aura: ${context.auraContext}\n")
                    append("Kai: ${context.kaiContext}\n")
                }

                // Update Vertex AI model context
                generativeModel.updateContext(combinedContext)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}
