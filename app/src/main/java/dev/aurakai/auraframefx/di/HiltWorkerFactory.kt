package dev.aurakai.auraframefx.di

import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WorkerModule {

    @Provides
    @Singleton
    fun provideHiltWorkerFactory(
        @ApplicationContext context: Context,
        workerFactory: HiltWorkerFactory,
    ): Configuration.Provider = object : Configuration.Provider {
        override fun getWorkManagerConfiguration(): Configuration =
            Configuration.Builder()
                .setMinimumLoggingLevel(android.util.Log.INFO)
                .setWorkerFactory(workerFactory)
                .build()
    }
}
