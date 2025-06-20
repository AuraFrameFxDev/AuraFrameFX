package dev.aurakai.auraframefx.ui.aicontent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.network.AuraFxContentApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for the AI Content generation features
 */
@HiltViewModel
public class AiContentViewModel @Inject constructor(
    private val contentApiClient: AuraFxContentApiClient,
) : ViewModel() {

    // UI states
    private val _textGenerationState =
        MutableStateFlow<TextGenerationState>(TextGenerationState.Idle)
    public val textGenerationState: StateFlow<TextGenerationState> = _textGenerationState

    private val _imageDescriptionState =
        MutableStateFlow<ImageDescriptionState>(ImageDescriptionState.Idle)
    public val imageDescriptionState: StateFlow<ImageDescriptionState> = _imageDescriptionState

    /**
     * Generates text content based on the provided prompt
     */
    public fun generateText(prompt: String, maxTokens: Int? = 500, temperature: Float? = 0.7f) {
        _textGenerationState.value = TextGenerationState.Loading

        viewModelScope.launch {
            try {
                public val response = contentApiClient.generateText(prompt, maxTokens, temperature)
                _textGenerationState.value = TextGenerationState.Success(
                    generatedText = response.generatedText ?: "",
                    finishReason = response.finishReason ?: ""
                )
            } catch (e: Exception) {
                Timber.e(e, "Error generating text")
                _textGenerationState.value = TextGenerationState.Error(e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Generates a description for an image at the provided URL
     */
    public fun generateImageDescription(imageUrl: String, context: String? = null) {
        _imageDescriptionState.value = ImageDescriptionState.Loading

        viewModelScope.launch {
            try {
                public val response = contentApiClient.generateImageDescription(imageUrl, context)
                _imageDescriptionState.value = ImageDescriptionState.Success(
                    description = response.description ?: ""
                )
            } catch (e: Exception) {
                Timber.e(e, "Error generating image description")
                _imageDescriptionState.value =
                    ImageDescriptionState.Error(e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Reset the text generation state to idle
     */
    public fun resetTextGenerationState() {
        _textGenerationState.value = TextGenerationState.Idle
    }

    /**
     * Reset the image description state to idle
     */
    public fun resetImageDescriptionState() {
        _imageDescriptionState.value = ImageDescriptionState.Idle
    }
}

/**
 * State class for text generation
 */
public sealed class TextGenerationState {
    public object Idle : TextGenerationState()
    public object Loading : TextGenerationState()
    public data class Success(val generatedText: String, val finishReason: String) : TextGenerationState()
    public data class Error(val errorMessage: String) : TextGenerationState()
}

/**
 * State class for image description generation
 */
public sealed class ImageDescriptionState {
    public object Idle : ImageDescriptionState()
    public object Loading : ImageDescriptionState()
    public data class Success(val description: String) : ImageDescriptionState()
    public data class Error(val errorMessage: String) : ImageDescriptionState()
}
