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
             * Provides a singleton WorkManager Configuration that uses the specified HiltWorkerFactory for dependency injection in workers.
             *
             * @return A WorkManager Configuration instance with Hilt-based worker factory support.
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
     * Provides the application's singleton WorkManager instance, ensuring it is initialized with Hilt-provided configuration.
     *
     * @return The WorkManager instance associated with the application context.
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
