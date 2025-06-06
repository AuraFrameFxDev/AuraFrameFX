package com.example.app.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.app.ai.AuraAIService // Assuming path from previous creation
import com.example.app.ai.AuraAIServiceImpl // Added import
import com.example.app.ai.config.AIConfig // Assuming path from previous creation
import com.example.app.data.SecurePreferences // Assuming path from previous creation
import com.google.ai.client.generativeai.GenerativeModel // Added import
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt AppModule for providing application-wide dependencies.
 * TODO: Reported as unused declaration. Ensure Hilt is set up and this module is processed.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Provides the application context.
     * @param application The application instance. (Hilt provides this, not typically marked as unused by user).
     * TODO: Method reported as unused. Verify necessity.
     */
    @Provides
    @Singleton
    fun provideApplicationContext(application: Application): Context {
        // Hilt provides Application, so no underscore needed for 'application' parameter itself.
        return application.applicationContext
    }

    /**
     * Provides SharedPreferences.
     * @param _context Application context.
     * TODO: Method reported as unused. Implement if SharedPreferences are used.
     */
    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        // TODO: Parameter context was reported as _context and unused (Hilt will provide it).
        // Renamed to context from _context for clarity in usage.
        return context.getSharedPreferences("AuraFrameFX_prefs", Context.MODE_PRIVATE)
    }

    /**
     * Provides SecurePreferences.
     * @param context Application context.
     * TODO: Method reported as unused. Implement if SecurePreferences is used.
     */
    @Provides
    @Singleton
    fun provideSecurePreferences(@ApplicationContext context: Context): SecurePreferences {
        // TODO: Parameter context was reported as _context and unused (Hilt will provide it).
        // Renamed to context from _context for clarity in usage.
        return SecurePreferences(context)
    }

    /**
     * Provides a generic AIConfig.
     * TODO: Method reported as unused. Implement if a global AIConfig is needed.
     */
    @Provides
    @Singleton
    fun provideAIConfig(): AIConfig {
        // TODO: Load actual config values from a secure source or build config.
        return AIConfig(
            modelName = "gemini-pro",
            apiKey = "TODO_load_api_key",
            projectId = "TODO_load_project_id"
        )
    }

    /**
     * Placeholder for AIConfigFactory - Name taken from error report list for AppModule.
     * TODO: Method reported as unused. Define AIConfigFactory and implement.
     */
    @Provides
    @Singleton
    fun provideAIConfigFactory(): Any? { // Using Any as AIConfigFactory type placeholder
        // TODO: Define AIConfigFactory class and return an instance.
        return null
    }

    /**
     * Provides AuraAIService.
     * @param _config AIConfig dependency.
     * TODO: Method reported as unused. Implement if AuraAIService is used.
     */
    @Provides
    @Singleton // Ensure singleton scope if AuraAIServiceImpl is @Singleton
    fun provideAuraAIService(impl: AuraAIServiceImpl): AuraAIService {
        // TODO: Method reported as unused. Verify necessity.
        // Hilt will provide AuraAIServiceImpl due to its @Inject constructor.
        // This method binds the implementation to the interface.
        return impl
    }

    /**
     * Provides a GenerativeModel (specific type unknown, using Any).
     * TODO: Method reported as unused. Define actual GenerativeModel type and provide.
     */
    @Provides
    @Singleton
    fun provideGenerativeModel(): GenerativeModel? { // Changed return type from Any?
        // TODO: Actually provide the GenerativeModel instance
        // Example:
        // val config = VertexAIConfig(apiKey = "YOUR_API_KEY") // Or inject VertexAIConfig
        // val vertexAI = VertexAI.Builder().setVertexAIConfig(config).build() // This is a different VertexAI
        // return com.google.ai.client.generativeai.GenerativeModel(modelName = "gemini-pro", apiKey = "YOUR_API_KEY") // Simplified example
        return null // Placeholder
    }
}
