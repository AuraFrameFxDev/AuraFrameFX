package dev.aurakai.auraframefx

import android.app.Application
import android.util.Log // Added import
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
public class AuraFrameApplication(override val workManagerConfiguration: Configuration) : Application(), Configuration.Provider {
    override fun onCreate() {
        super.onCreate()
        // Initialization code here
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setMinimumLoggingLevel(Log.INFO)
            .build()
    }
}
