package dev.aurakai.auraframefx.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ApplicationScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@ApplicationScoped
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val dataStore = context.dataStore

    companion object {
        private val API_KEY = stringPreferencesKey("api_key")
        private val USER_ID = stringPreferencesKey("user_id")
        private val USER_NAME = stringPreferencesKey("user_name")
        private val USER_EMAIL = stringPreferencesKey("user_email")
    }

    // API Key
    val apiKey: Flow<String?> = dataStore.data.map { preferences ->
        preferences[API_KEY]
    }

    suspend fun setApiKey(apiKey: String) {
        dataStore.edit { preferences ->
            preferences[API_KEY] = apiKey
        }
    }

    // User ID
    val userId: Flow<String?> = dataStore.data.map { preferences ->
        preferences[USER_ID]
    }

    suspend fun setUserId(userId: String) {
        dataStore.edit { preferences ->
            preferences[USER_ID] = userId
        }
    }

    // User Name
    val userName: Flow<String?> = dataStore.data.map { preferences ->
        preferences[USER_NAME]
    }

    suspend fun setUserName(userName: String) {
        dataStore.edit { preferences ->
            preferences[USER_NAME] = userName
        }
    }

    // User Email
    val userEmail: Flow<String?> = dataStore.data.map { preferences ->
        preferences[USER_EMAIL]
    }

    suspend fun setUserEmail(userEmail: String) {
        dataStore.edit { preferences ->
            preferences[USER_EMAIL] = userEmail
        }
    }

    // Convenience method to get all user data at once
    suspend fun getUserData() = runBlocking {
        dataStore.data.first().let { preferences ->
            UserData(
                apiKey = preferences[API_KEY],
                userId = preferences[USER_ID],
                userName = preferences[USER_NAME],
                userEmail = preferences[USER_EMAIL]
            )
        }
    }

    data class UserData(
        val apiKey: String?,
        val userId: String?,
        val userName: String?,
        val userEmail: String?,
    )
