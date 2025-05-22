package dev.aurakai.auraframefx.di

import android.content.Context
import com.google.ai.generativeai.GenerativeModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.ai.VertexAIClient
import dev.aurakai.auraframefx.ai.VertexAIConfig
import dev.aurakai.auraframefx.ai.VertexAIManager
import javax.inject.Singleton

/**
 * Dagger Hilt module that provides dependencies related to Vertex AI.
 */
@Module
@InstallIn(SingletonComponent::class)
object VertexAIModule {

    /**
     * Provides the Vertex AI configuration.
     * You can customize the default values based on your needs.
     */
    @Provides
    @Singleton
    fun provideVertexAIConfig(): VertexAIConfig {
        return VertexAIConfig(
            projectId = "YOUR_PROJECT_ID",  // Replace with your actual project ID
            location = "us-central1",        // Update with your preferred region
            modelName = "gemini-pro",        // Or your preferred model
            apiKey = null,                    // Set to null to use application default credentials
            temperature = 0.7f,
            topK = 40,
            topP = 0.95f,
            maxOutputTokens = 2048
        )
    }

    /**
     * Provides the VertexAIClient as a singleton.
     */
    @Provides
    @Singleton
    fun provideVertexAIClient(
        @ApplicationContext context: Context,
        config: VertexAIConfig
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
     * Provides the GenerativeModel directly if needed elsewhere in the app.
     */
    @Provides
    @Singleton
    fun provideGenerativeModel(config: VertexAIConfig): GenerativeModel {
        return GenerativeModel(
            modelName = config.modelName,
            apiKey = config.apiKey,
            generationConfig = com.google.ai.generativeai.type.schema.generationConfig {
                temperature = config.temperature
                topK = config.topK
                topP = config.topP
                maxOutputTokens = config.maxOutputTokens
            }
        )
    }
    
    /**
     * Provides the VertexAIManager as a singleton.
     */
    @Provides
    @Singleton
    fun provideVertexAIManager(
        @ApplicationContext context: Context,
        vertexAIClient: VertexAIClient
    ): VertexAIManager {
        return VertexAIManager(context, vertexAIClient)
    }
}
