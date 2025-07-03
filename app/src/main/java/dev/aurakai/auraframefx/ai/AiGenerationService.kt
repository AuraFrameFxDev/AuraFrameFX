package dev.aurakai.auraframefx.ai

import dev.aurakai.auraframefx.generated.api.auraframefxai.ContentApi
import dev.aurakai.auraframefx.generated.model.auraframefxai.GenerateImageDescriptionRequest
import dev.aurakai.auraframefx.generated.model.auraframefxai.GenerateImageDescriptionResponse
import dev.aurakai.auraframefx.generated.model.auraframefxai.GenerateTextRequest
import dev.aurakai.auraframefx.generated.model.auraframefxai.GenerateTextResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AiGenerationService(
    private val api: ContentApi
) {
    /**
     * Asynchronously generates AI-driven text based on the provided prompt.
     *
     * Executes the request on the IO dispatcher and returns a [Result] containing the generated text response or an exception if the operation fails.
     *
     * @param prompt The input text prompt to guide the AI text generation.
     * @param maxTokens The maximum number of tokens to generate. Defaults to 500.
     * @param temperature The sampling temperature for generation, controlling randomness. Defaults to 0.7.
     * @return A [Result] containing the [GenerateTextResponse] on success, or an exception on failure.
     */
    suspend fun generateText(prompt: String, maxTokens: Int = 500, temperature: Float = 0.7f): Result<GenerateTextResponse> = withContext(Dispatchers.IO) {
        try {
            val request = GenerateTextRequest(prompt = prompt, maxTokens = maxTokens, temperature = temperature)
            val response = api.generateText(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Generates a description for the specified image using AI, optionally incorporating additional context.
     *
     * @param imageUrl The URL of the image to describe.
     * @param context Optional context to guide the description generation.
     * @return A [Result] containing the generated image description on success, or an exception on failure.
     */
    suspend fun generateImageDescription(imageUrl: String, context: String? = null): Result<GenerateImageDescriptionResponse> = withContext(Dispatchers.IO) {
        try {
            val request = GenerateImageDescriptionRequest(imageUrl = imageUrl, context = context)
            val response = api.generateImageDescription(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
