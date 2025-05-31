package dev.aurakai.auraframefx.di

import android.app.Application
import android.content.Context
import androidx.startup.Initializer
import androidx.work.Configuration
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.aurakai.auraframefx.ai.KaiController
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles application-level initialization logic.
 * This class is responsible for initializing various components when the application starts.
 */
@Singleton
class AppInitializer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val kaiController: KaiController,
) {
    fun initialize(application: Application) {
        Timber.d("Initializing application components...")

        // Initialize components
        initializeNativeLibraries()
        initializeCrashReporting()
        initializeAnalytics()
        initializeKaiNotchBar()

        Timber.d("Application components initialized successfully")
    }

    private fun initializeNativeLibraries() {
        try {
            Timber.d("Initializing native libraries... (skipped for debug build)")
            // Temporarily disabled for debug build
            // System.loadLibrary("AuraFrameFX")
            Timber.d("Native libraries initialization skipped")
        } catch (e: UnsatisfiedLinkError) {
            Timber.e(e, "Failed to load native library")
            // We might want to handle this differently in production
            // Disabled for now
            // if (BuildConfig.DEBUG) {
            //     throw e
            // }
        }
    }

    private fun initializeCrashReporting() {
        try {
            Timber.d("Initializing crash reporting...")
            // Initialize crash reporting here (e.g., Firebase Crashlytics)
            // FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
            Timber.d("Crash reporting initialized")
        } catch (e: Exception) {
            Timber.e(e, "Failed to initialize crash reporting")
        }
    }

    private fun initializeAnalytics() {
        try {
            Timber.d("Initializing analytics...")
            // Initialize analytics here (e.g., Firebase Analytics)
            // Firebase.analytics.setAnalyticsCollectionEnabled(!BuildConfig.DEBUG)
            Timber.d("Analytics initialized")
        } catch (e: Exception) {
            Timber.e(e, "Failed to initialize analytics")
        }
    }

    private fun initializeKaiNotchBar() {
        try {
            Timber.d("Initializing Kai Notch Bar...")
            kaiController.initialize(context)
            Timber.d("Kai Notch Bar initialized successfully")
        } catch (e: Exception) {
            Timber.e(e, "Failed to initialize Kai Notch Bar")
        }
    }
}

/**
 * Initializer for the application to ensure proper initialization order.
 */
class AppInitializerInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        // No need to do anything here as AppInitializer is managed by Hilt
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = listOf(
        WorkManagerInitializer::class.java
    )
}

/**
 * Initializer for WorkManager to ensure it's properly initialized at app startup.
 */
class WorkManagerInitializer : Initializer<WorkManager> {
    override fun create(context: Context): WorkManager {
        val configuration = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()

        // Initialize WorkManager with the default configuration
        WorkManager.initialize(context, configuration)
        return WorkManager.getInstance(context)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
