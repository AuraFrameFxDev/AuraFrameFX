package dev.aurakai.auraframefx.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dev.aurakai.auraframefx.model.UserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// Define a DataStore instance using the extension property from DataStoreManager.kt
// The actual definition `val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")`
// is expected to be in DataStoreManager.kt or a similar central place.
// For UserPreferences, we assume it's available via context.dataStore.
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    private val dataStore = context.dataStore

    companion object {
        val API_KEY = stringPreferencesKey("api_key")
        val USER_ID = stringPreferencesKey("user_id")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
    }

    val apiKey: Flow<String?> = dataStore.data.map { preferences ->
        preferences[API_KEY]
    }

    suspend fun setApiKey(key: String?) {
        dataStore.edit { settings ->
            if (key != null) {
                settings[API_KEY] = key
            } else {
                settings.remove(API_KEY)
            }
        }
    }

    val userId: Flow<String?> = dataStore.data.map { preferences ->
        preferences[USER_ID]
    }

    suspend fun setUserId(id: String?) {
        dataStore.edit { settings ->
            if (id != null) {
                settings[USER_ID] = id
            } else {
                settings.remove(USER_ID)
            }
        }
    }

    val userName: Flow<String?> = dataStore.data.map { preferences ->
        preferences[USER_NAME]
    }

    suspend fun setUserName(name: String?) {
        dataStore.edit { settings ->
            if (name != null) {
                settings[USER_NAME] = name
            } else {
                settings.remove(USER_NAME)
            }
        }
    }

    val userEmail: Flow<String?> = dataStore.data.map { preferences ->
        preferences[USER_EMAIL]
    }

    suspend fun setUserEmail(email: String?) {
        dataStore.edit { settings ->
            if (email != null) {
                settings[USER_EMAIL] = email
            } else {
                settings.remove(USER_EMAIL)
            }
        }
    }

    /**
     * Retrieves user data by collecting the latest values from DataStore.
     * @return UserData object or null if essential data is missing.
     */
    suspend fun getUserData(): UserData? {
        val currentApiKey = apiKey.first()
        val currentUserId = userId.first()
        val currentUserName = userName.first()
        val currentUserEmail = userEmail.first()

        // Example: Consider returning null or a UserData object with nullable fields
        // if not all data is critical. For this example, let's assume all are optional.
        return UserData(
            id = currentUserId,
            name = currentUserName,
            email = currentUserEmail,
            apiKey = currentApiKey
        )
    }
}
