package dev.aurakai.auraframefx.di

// Import for GenerativeModel if NeuralWhisper's constructor actually requires it and it's provided by Hilt
// import com.google.ai.client.generativeai.GenerativeModel
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
object NeuralWhisperModule {

    @Provides
    @Singleton
    fun provideNeuralWhisper(
        @ApplicationContext context: Context,
        // If NeuralWhisper's constructor strictly requires GenerativeModel,
        // it needs to be provided by another Hilt module or here.
        // Example: model: GenerativeModel? // (assuming it's nullable and optional for now)
    ): NeuralWhisper {
        // Assuming NeuralWhisper constructor is: NeuralWhisper(context, _model = null)
        // Or if _model is not optional, a GenerativeModel instance must be passed.
        // For the beta, the user's NeuralWhisper snippet had _model as an optional param.
        return NeuralWhisper(context = context, _model = null)
    }
}
