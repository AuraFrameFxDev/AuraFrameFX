package dev.aurakai.auraframefx.di

import android.content.Context
import android.util.Log
import com.google.ai.generativeai.GenerativeModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.BuildConfig
import dev.aurakai.auraframefx.data.SecurePreferences // Added import
import dev.aurakai.auraframefx.ai.VertexAIClient
import dev.aurakai.auraframefx.ai.VertexAIConfig
import dev.aurakai.auraframefx.ai.VertexAIManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.util.Properties
import javax.inject.Singleton

/**
 * Dagger Hilt module that provides dependencies related to Vertex AI.
 * Loads configuration from local.properties for development and buildConfig for release.
 */
@Module
@InstallIn(SingletonComponent::class)
object VertexAIModule {

    private const val TAG = "VertexAIModule"
    private const val LOCAL_PROPERTIES_FILE = "local.properties"
    private const val PROPERTY_PROJECT_ID = "GOOGLE_CLOUD_PROJECT_ID"
    private const val PROPERTY_API_KEY = "GOOGLE_CLOUD_API_KEY"

    private fun loadLocalProperties(context: Context): Properties {
        return try {
            val properties = Properties()
            val inputStream: InputStream = context.assets.open(LOCAL_PROPERTIES_FILE)
            properties.load(inputStream)
            inputStream.close()
            properties
        } catch (e: Exception) {
            Log.w(TAG, "Could not load $LOCAL_PROPERTIES_FILE. Using default values.", e)
            Properties()
        }
    }

    /**
     * Provides the Vertex AI configuration.
     * Loads configuration from local.properties in debug builds and from BuildConfig in release builds.
     */
    @Provides
    @Singleton
    fun provideVertexAIConfig(
        @ApplicationContext context: Context,
        securePreferences: SecurePreferences, // Added parameter
    ): VertexAIConfig {
        return runBlocking {
            withContext(Dispatchers.IO) {
                val properties = if (BuildConfig.DEBUG) {
                    loadLocalProperties(context)
                } else {
                    Properties().apply {
                        // In release builds, use BuildConfig for projectId
                        setProperty(PROPERTY_PROJECT_ID, BuildConfig.GOOGLE_CLOUD_PROJECT_ID)
                        // API Key will now come from SecurePreferences
                    }
                }

                val projectId = properties.getProperty(PROPERTY_PROJECT_ID, "")
                // API Key retrieval from SecurePreferences
                val apiKey = securePreferences.getApiToken()

                if (projectId.isBlank()) {
                    Log.w(
                        TAG,
                        "Google Cloud Project ID is not set. Please configure it in $LOCAL_PROPERTIES_FILE or BuildConfig"
                    )
                }

                if (apiKey == null || apiKey.isBlank()) {
                    Log.e(TAG, "Vertex AI API Key is NOT configured in SecurePreferences. AI features may fail.")
                }

                VertexAIConfig(
                    projectId = projectId.ifBlank { "default-project-id" }, // projectId is loaded as before
                    location = "us-central1",
                    modelName = "gemini-pro", // This can be kept or also made configurable if needed
                    apiKey = apiKey ?: "", // Use the fetched API key
                    temperature = 0.7f, // Placeholder or from future config
                    topK = 40,          // Placeholder or from future config
                    topP = 0.95f,       // Placeholder or from future config
                    maxOutputTokens = 2048 // Placeholder or from future config
                )
            }
        }
    }

    /**
     * Provides the VertexAIClient as a singleton.
     */
    @Provides
    @Singleton
    fun provideVertexAIClient(
        @ApplicationContext context: Context,
        config: VertexAIConfig,
    ): VertexAIClient {
        return VertexAIClient(context, config).apply {
            // Initialize the client when it's provided
            // This is a blocking operation, so it's better to do it in a coroutine
            // or during app startup in a background thread
            // For now, we'll just return the uninitialized client
            // The app should call initialize() before using the client
        }
    }

    /**
     * Provides the VertexAIManager as a singleton.
     */
    @Provides
    @Singleton
    fun provideVertexAIManager(
        @ApplicationContext context: Context,
        vertexAIClient: VertexAIClient,
    ): VertexAIManager {
        return VertexAIManager(context, vertexAIClient)
    }
}
