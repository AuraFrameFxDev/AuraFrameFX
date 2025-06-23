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
    /**
         * Provides a singleton instance of KaiAIService using the given SecurityContext.
         *
         * @param securityContext The security context required to construct the KaiAIService implementation.
         * @return A singleton KaiAIService instance.
         */

    @Provides
    @Singleton
    fun provideKaiAIService(securityContext: SecurityContext): KaiAIService =
        KaiAIServiceImpl(securityContext) /**
     * Provides a singleton instance of CascadeAIService using the supplied AuraAIService and KaiAIService implementations.
     *
     * @param auraService The AuraAIService implementation to be used by CascadeAIServiceImpl.
     * @param kaiService The KaiAIService implementation to be used by CascadeAIServiceImpl.
     * @return A singleton CascadeAIService instance.
     */

    @Provides
    @Singleton
    fun provideCascadeAIService(
        auraService: AuraAIService, // Hilt will provide AuraAIServiceImpl
        kaiService: KaiAIService    // Hilt will provide KaiAIService from the method above
    ): CascadeAIService = CascadeAIServiceImpl(auraService, kaiService)
}