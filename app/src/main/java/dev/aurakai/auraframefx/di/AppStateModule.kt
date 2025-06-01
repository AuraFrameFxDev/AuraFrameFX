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
import dev.aurakai.auraframefx.data.state.AppStateManager
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_state")

@Module
@InstallIn(SingletonComponent::class)
object AppStateModule {

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    fun provideAppStateManager(
        dataStore: DataStore<Preferences>,
        @IoDispatcher ioDispatcher: kotlinx.coroutines.CoroutineDispatcher,
    ): AppStateManager {
        return AppStateManager(dataStore, ioDispatcher)
    }
}
