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

    /**
     * Sets or removes the stored API key in user preferences.
     *
     * If a non-null key is provided, it is saved; if null, the API key is removed from preferences.
     */
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

    /**
     * Updates the stored user ID in preferences.
     *
     * If `id` is non-null, sets the user ID to the provided value; if null, removes the user ID from preferences.
     */
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

    /**
     * Updates the stored user name in preferences.
     *
     * If `name` is non-null, sets the user name to the provided value; if null, removes the user name from preferences.
     */
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

    /**
     * Updates the stored user email address in persistent preferences.
     *
     * If `email` is non-null, sets the user email to the provided value; if null, removes the email from preferences.
     */
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
     * Retrieves the current user data from DataStore as a UserData object.
     *
     * @return A UserData instance containing the latest values for user ID, name, email, and API key, with all fields nullable if not set.
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
