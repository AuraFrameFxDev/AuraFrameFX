package dev.aurakai.auraframefx.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.ai.services.AuraAIService
import dev.aurakai.auraframefx.ai.services.AuraAIServiceImpl
import dev.aurakai.auraframefx.ai.services.CascadeAIService
import dev.aurakai.auraframefx.ai.services.CascadeAIServiceImpl
import dev.aurakai.auraframefx.ai.services.KaiAIService
import dev.aurakai.auraframefx.ai.services.KaiAIServiceImpl
import dev.aurakai.auraframefx.security.SecurityContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AiServiceModule {
    // AuraAIServiceImpl has an @Inject constructor, so Hilt can provide it directly.
    // No need for a @Provides method here if its dependencies are also provided.

    @Provides
    @Singleton
    fun provideKaiAIService(securityContext: SecurityContext): KaiAIService =
        KaiAIServiceImpl(securityContext) // Assuming KaiAIServiceImpl also has @Inject or is provided elsewhere if it has deps

    @Provides
    @Singleton
    fun provideCascadeAIService(
        auraService: AuraAIService, // Hilt will provide AuraAIServiceImpl
        kaiService: KaiAIService    // Hilt will provide KaiAIService from the method above
    ): CascadeAIService = CascadeAIServiceImpl(auraService, kaiService)
}