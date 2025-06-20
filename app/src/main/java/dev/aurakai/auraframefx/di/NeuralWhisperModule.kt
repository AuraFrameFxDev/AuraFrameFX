package dev.aurakai.auraframefx.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.ai.services.NeuralWhisper
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
public object NeuralWhisperModule {

    @Provides
    @Singleton
    public fun provideNeuralWhisper(
        @ApplicationContext context: Context,
    ): NeuralWhisper {
        return NeuralWhisper()
    }
}
