package dev.aurakai.auraframefx.di

import com.google.generativeai.GenerativeModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.ai.VertexAIClient
import dev.aurakai.auraframefx.data.vertexai.GenKitConfig
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object VertexModule {
    @Provides
    @Singleton
    fun provideGenKitConfig(
        userPreferences: UserPreferences,
    ): GenKitConfig = GenKitConfig(userPreferences)

    @Provides
    @Singleton
    fun provideGenerativeModel(): GenerativeModel {
        return GenerativeModel(
            modelName = "gemini-pro",
            // In a real app, get this from a secure source like BuildConfig or secure storage
            apiKey = ""
        )
    }

    @Provides
    @Singleton
    fun provideVertexAIClient(
        generativeModel: GenerativeModel,
    ): VertexAIClient = VertexAIClient(generativeModel)
}
