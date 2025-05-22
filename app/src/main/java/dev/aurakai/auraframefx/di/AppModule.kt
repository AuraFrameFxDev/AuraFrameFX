package dev.aurakai.auraframefx.di

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.google.ai.client.generativeai.GenerativeModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.ai.AuraAIService
import dev.aurakai.auraframefx.ai.AuraAIServiceImpl
import dev.aurakai.auraframefx.data.preferences.SecurePreferences
import dev.aurakai.auraframefx.ui.viewmodel.AIFeaturesViewModel
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApplicationContext(
        @ApplicationContext context: Context,
    ): Context = context

    @Provides
    @Singleton
    fun provideSharedPreferences(
        @ApplicationContext context: Context,
    ): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    @Provides
    @Singleton
    fun provideAppInitializer(
        @ApplicationContext context: Context,
    ): AppInitializer = AppInitializer(context)

    @Provides
    @Singleton
    fun provideSecurePreferences(
        @ApplicationContext context: Context,
    ): SecurePreferences = SecurePreferences(context)

    @Provides
    @Singleton
    fun provideGenerativeModel(
        securePrefs: SecurePreferences,
    ): GenerativeModel {
        val apiKey = securePrefs.getString(SecurePreferences.KEY_AI_API_KEY, "")
        return GenerativeModel(
            modelName = "gemini-pro",
            apiKey = apiKey
        )
    }

    @Provides
    @Singleton
    fun provideAuraAIService(
        generativeModel: GenerativeModel,
        securePrefs: SecurePreferences,
    ): AuraAIService {
        return AuraAIServiceImpl(generativeModel, securePrefs)
    }

    @Provides
    @Singleton
    fun provideAIConfigFactory(
        @ApplicationContext context: Context,
        securePrefs: SecurePreferences,
    ): AIFeaturesViewModel.AIConfigFactory {
        return AIFeaturesViewModel.AIConfigFactory(context, securePrefs)
    }
}
