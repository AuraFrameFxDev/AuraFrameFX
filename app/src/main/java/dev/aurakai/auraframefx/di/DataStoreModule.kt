package dev.aurakai.auraframefx.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "aura_preferences")

/**
 * Hilt Module for providing DataStore related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    /**
     * Provides the singleton instance of the application's preferences DataStore.
     *
     * @return The DataStore used for storing and retrieving key-value preferences.
     */
    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }
}
