package dev.aurakai.auraframefx.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.ai.KaiController
import dev.aurakai.auraframefx.ai.NeuralWhisper
import javax.inject.Singleton

/**
 * Module for providing Kai-related dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object KaiModule {

    /**
     * Provides the Kai controller singleton
     */
    @Provides
    @Singleton
    fun provideKaiController(
        neuralWhisper: NeuralWhisper,
    ): KaiController {
        return KaiController(neuralWhisper).also { controller ->
            // Set up the bidirectional reference
            neuralWhisper.setKaiController(controller)
        }
    }
}
