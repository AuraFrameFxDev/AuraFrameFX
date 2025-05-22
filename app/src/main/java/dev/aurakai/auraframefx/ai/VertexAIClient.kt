package dev.aurakai.auraframefx.ai

import com.google.ai.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A client for interacting with Google's Generative AI models.
 * This class handles the initialization of the AI models and provides methods for generating content.
 */
@Singleton
class VertexAIClient @Inject constructor(
    private val generativeModel: GenerativeModel,
) {
    /**
     * Generates content based on the provided prompt using the configured AI model.
     *
     * @param prompt The input prompt for content generation.
     * @return A [Result] containing either the generated content or an exception.
     */
    suspend fun generateContent(prompt: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                generativeModel.generateContent(prompt).text
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    /**
     * Observes the content generation process as a Flow.
     *
     * @param prompt The input prompt for content generation.
     * @return A [Flow] emitting the generated content chunks.
     */
    fun observeContentGeneration(prompt: String): Flow<Result<String>> = flow {
        try {
            val response = generativeModel.generateContentStream(prompt)
            response.collect { generateContentResponse ->
                val text = generateContentResponse.text
                if (text != null) {
                    emit(Result.success(text))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)
}
