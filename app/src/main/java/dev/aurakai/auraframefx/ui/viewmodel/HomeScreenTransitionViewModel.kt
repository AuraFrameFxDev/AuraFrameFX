package dev.aurakai.auraframefx.ui.viewmodel

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.aurakai.auraframefx.system.homescreen.HomeScreenTransitionConfig
import dev.aurakai.auraframefx.system.homescreen.HomeScreenTransitionEffect
import dev.aurakai.auraframefx.system.homescreen.HomeScreenTransitionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException
import javax.inject.Inject

private const val PREF_TRANSITION_CONFIG = "home_screen_transition"

// Extension function to get serializer for HomeScreenTransitionConfig
@OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
/**
 * Serializes this HomeScreenTransitionConfig instance to a JSON string.
 *
 * @return The JSON representation of the configuration.
 */
private fun HomeScreenTransitionConfig.toJson(): String {
    return Json.encodeToString(this)
}

@OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
/**
 * Attempts to deserialize this JSON string into a HomeScreenTransitionConfig object.
 *
 * @return The deserialized HomeScreenTransitionConfig, or null if deserialization fails.
 */
private fun String.toHomeScreenTransitionConfig(): HomeScreenTransitionConfig? {
    return try {
        Json.decodeFromString<HomeScreenTransitionConfig>(this)
    } catch (e: Exception) {
        null
    }
}

@HiltViewModel
class HomeScreenTransitionViewModel @Inject constructor(
    private val transitionManager: HomeScreenTransitionManager,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {
    
    private val json = Json { ignoreUnknownKeys = true }
    private val _currentConfig = MutableStateFlow<HomeScreenTransitionConfig?>(null)
    val currentConfig: StateFlow<HomeScreenTransitionConfig?> = _currentConfig

    init {
        viewModelScope.launch {
            transitionManager.currentConfig.collect { config ->
                _currentConfig.value = config
            }
        }
    }
    
    /**
     * Updates the current home screen transition configuration based on the provided properties map.
     *
     * If the map contains "defaultOutgoingEffect" or "defaultIncomingEffect", updates the corresponding effect in the configuration.
     * The updated configuration is persisted to DataStore preferences.
     *
     * @param properties A map containing transition property keys and their new values.
     */
    fun updateTransitionProperties(properties: Map<String, Any>) {
        viewModelScope.launch {
            val current = _currentConfig.value ?: return@launch
            
            val updatedConfig = when {
                properties.containsKey("defaultOutgoingEffect") -> {
                    val effect = properties["defaultOutgoingEffect"] as? HomeScreenTransitionEffect
                    current.copy(defaultOutgoingEffect = effect ?: current.defaultOutgoingEffect)
                }
                properties.containsKey("defaultIncomingEffect") -> {
                    val effect = properties["defaultIncomingEffect"] as? HomeScreenTransitionEffect
                    current.copy(defaultIncomingEffect = effect ?: current.defaultIncomingEffect)
                }
                else -> current
            }
            
            _currentConfig.update { updatedConfig }
            
            // Save to preferences
            saveConfigToPreferences(updatedConfig)
        }
    }
    
    /**
     * Resets the home screen transition configuration to its default values and persists the updated configuration to preferences.
     */
    fun resetToDefault() {
        viewModelScope.launch {
            transitionManager.resetToDefault()
            _currentConfig.value?.let { saveConfigToPreferences(it) }
        }
    }
    
    /**
     * Saves the provided home screen transition configuration to DataStore preferences as a JSON string.
     *
     * Handles IO exceptions by printing the stack trace.
     */
    private suspend fun saveConfigToPreferences(config: HomeScreenTransitionConfig) {
        try {
            dataStore.edit { preferences ->
                preferences[stringPreferencesKey(PREF_TRANSITION_CONFIG)] = config.toJson()
            }
        } catch (e: IOException) {
            // Handle error, e.g., log it
            e.printStackTrace()
        }
    }
    
    /**
     * Loads the home screen transition configuration from DataStore preferences.
     *
     * @return The deserialized HomeScreenTransitionConfig if available and valid, or null if not found or deserialization fails.
     */
    suspend fun loadConfigFromPreferences(): HomeScreenTransitionConfig? {
        return try {
            dataStore.data
                .catch { exception ->
                    // Handle error, e.g., log it
                    if (exception is IOException) {
                        emit(emptyPreferences())
                    } else {
                        throw exception
                    }
                }
                .map { preferences ->
                    preferences[stringPreferencesKey(PREF_TRANSITION_CONFIG)]?.toHomeScreenTransitionConfig()
                }
                .first()
        } catch (e: Exception) {
            // Handle error, e.g., log it
            e.printStackTrace()
            null
        }
    }
}
