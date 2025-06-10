package dev.aurakai.auraframefx

import android.app.Application
import androidx.work.Configuration // Added import for Configuration
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp // Added annotation
// TODO: Class AuraFrameApplication reported as unused. Verify usage or remove if truly obsolete.
class AuraFrameApplication :
    Application() { // Potentially also implements androidx.work.Configuration.Provider
    override fun onCreate() {
        super.onCreate()
        // TODO: Reported as unused entry point. Verify usage or remove if truly obsolete.
        // Initialization code here
    }

    // This method is part of androidx.work.Configuration.Provider interface
    // If this class is meant to provide WorkManager config, it should implement the interface.
    // fun getWorkManagerConfiguration(): androidx.work.Configuration {
    //     // TODO: Reported as unused. Implement if this app provides custom WorkManager config.
    //     return androidx.work.Configuration.Builder()
    //         .setMinimumLoggingLevel(android.util.Log.INFO)
    //         .build()
    // }
    // For now, adding as a simple method as per instruction, not tied to interface yet.
    fun getWorkManagerConfiguration(): Configuration { // Used Configuration import
        // TODO: Reported as unused. Implement if this app provides custom WorkManager config.
        // This might be part of implementing androidx.work.Configuration.Provider
        return Configuration.Builder().build()
    }
}
