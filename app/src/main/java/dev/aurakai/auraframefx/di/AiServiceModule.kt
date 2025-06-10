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
    @Provides
    @Singleton
    fun provideAuraAIService(): AuraAIService = AuraAIServiceImpl()

    @Provides
    @Singleton
    fun provideKaiAIService(securityContext: SecurityContext): KaiAIService =
        KaiAIServiceImpl(securityContext)

    @Provides
    @Singleton
    fun provideCascadeAIService(): CascadeAIService = CascadeAIServiceImpl()
}