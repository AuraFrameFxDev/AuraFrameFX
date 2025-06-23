package dev.aurakai.auraframefx.di

import android.content.Context
import androidx.hilt.work.HiltWorkerFactory // For Configuration.Builder().setWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt Module for providing WorkManager related dependencies.
 * TODO: Reported as unused declaration. Ensure Hilt is set up for WorkManager.
 */
@Module
@InstallIn(SingletonComponent::class)
object WorkManagerModule {

    /**
             * Supplies a singleton WorkManager Configuration using the provided HiltWorkerFactory.
             *
             * Use this to customize WorkManager's initialization with a specific WorkerFactory.
             *
             * @param workerFactory The HiltWorkerFactory to be used by WorkManager.
             * @return A Configuration instance for WorkManager.
             */
    @Provides
    @Singleton
    fun provideWorkManagerConfiguration(
        workerFactory: HiltWorkerFactory,
    ): Configuration =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    /**
     * Provides a singleton instance of WorkManager.
     *
     * Returns the WorkManager instance associated with the application context. Assumes WorkManager is initialized by Hilt when a Configuration provider is present.
     *
     * @return The WorkManager instance.
     */
    @Provides
    @Singleton
    @Suppress("UNUSED_PARAMETER")
    fun provideWorkManager(
        @ApplicationContext _context: Context,
        _configuration: Configuration, // Hilt will provide this from the method above
    ): WorkManager {
        // TODO: Parameters _context, _configuration reported as unused (Hilt will provide them).
        // WorkManager.initialize(_context, _configuration) // This should be done once, usually in Application.onCreate
        // return WorkManager.getInstance(_context)

        // As per Hilt docs, if you provide Configuration, Hilt handles initialization.
        // So, just getting the instance should be fine.
        return WorkManager.getInstance(_context) // Placeholder, assumes Hilt handles init via Configuration provider
    }
}
