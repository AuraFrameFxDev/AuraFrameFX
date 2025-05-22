package dev.aurakai.auraframefx.ai

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.Dispatchers

private const val TAG = "VertexAIUtils"

/**
 * Extension function to handle errors in a Flow of Results.
 *
 * @param onError Callback for handling errors. Returns a fallback value.
 * @return A new Flow that emits the original values or the fallback value in case of an error.
 */
fun <T> Flow<Result<T>>.handleErrors(
    onError: (Throwable) -> T
): Flow<T> = this
    .map { result ->
        result.fold(
            onSuccess = { Result.success(it) },
            onFailure = { 
                Log.e(TAG, "Error in flow", it)
                Result.success(onError(it)) 
            }
        )
    }
    .catch { e ->
        Log.e(TAG, "Error in flow catch", e)
        emit(Result.success(onError(e)))
    }
    .map { it.getOrThrow() }
    .flowOn(Dispatchers.Default)

/**
 * Extension function to log errors in a Flow of Results.
 *
 * @param tag The tag to use for logging.
 * @return A new Flow that logs any errors that occur.
 */
fun <T> Flow<Result<T>>.logErrors(tag: String = TAG): Flow<Result<T>> = this
    .catch { e ->
        Log.e(tag, "Error in flow", e)
        emit(Result.failure(e))
    }

/**
 * Creates a safe configuration for the Vertex AI client with the provided parameters.
 *
 * @param projectId The Google Cloud project ID.
 * @param location The location/region of the Vertex AI service.
 * @param modelName The name of the model to use.
 * @param apiKey Optional API key for authentication.
 * @param temperature Controls randomness in the response generation.
 * @param topK The number of highest probability vocabulary tokens to keep for top-k filtering.
 * @param topP The cumulative probability for top-p filtering.
 * @param maxOutputTokens The maximum number of tokens in the generated response.
 * @return A [VertexAIConfig] instance with the provided parameters.
 */
fun createVertexAIConfig(
    projectId: String = "YOUR_PROJECT_ID",
    location: String = "us-central1",
    modelName: String = "gemini-pro",
    apiKey: String? = null,
    temperature: Float = 0.7f,
    topK: Int = 40,
    topP: Float = 0.95f,
    maxOutputTokens: Int = 2048
): VertexAIConfig {
    return VertexAIConfig(
        projectId = projectId,
        location = location,
        modelName = modelName,
        apiKey = apiKey,
        temperature = temperature.coerceIn(0f..1f),
        topK = topK.coerceAtLeast(1),
        topP = topP.coerceIn(0f..1f),
        maxOutputTokens = maxOutputTokens.coerceAtLeast(1)
    )
}

/**
 * Validates the Vertex AI configuration.
 *
 * @return A [Result] containing the configuration if valid, or an exception if invalid.
 */
fun VertexAIConfig.validate(): Result<VertexAIConfig> {
    return try {
        require(projectId.isNotBlank()) { "Project ID cannot be blank" }
        require(location.isNotBlank()) { "Location cannot be blank" }
        require(modelName.isNotBlank()) { "Model name cannot be blank" }
        require(temperature in 0f..1f) { "Temperature must be between 0 and 1" }
        require(topK > 0) { "topK must be greater than 0" }
        require(topP in 0f..1f) { "topP must be between 0 and 1" }
        require(maxOutputTokens > 0) { "maxOutputTokens must be greater than 0" }
        
        Result.success(this)
    } catch (e: Exception) {
        Log.e(TAG, "Invalid Vertex AI configuration", e)
        Result.failure(e)
    }
}

/**
 * Extension function to safely generate content with error handling.
 *
 * @param prompt The input prompt for content generation.
 * @param temperature Controls randomness in the response generation.
 * @param maxOutputTokens The maximum number of tokens in the generated response.
 * @param topK The number of highest probability vocabulary tokens to keep for top-k filtering.
 * @param topP The cumulative probability for top-p filtering.
 * @return A [Result] containing either the generated content or an exception.
 */
suspend fun VertexAIClient.safeGenerateContent(
    prompt: String,
    temperature: Float = this.getConfig().temperature,
    maxOutputTokens: Int = this.getConfig().maxOutputTokens,
    topK: Int = this.getConfig().topK,
    topP: Float = this.getConfig().topP
): Result<String> {
    return try {
        if (!isInitialized()) {
            Result.failure(IllegalStateException("VertexAIClient not initialized. Call initialize() first."))
        } else {
            generateContent(prompt, temperature, maxOutputTokens, topK, topP)
        }
    } catch (e: Exception) {
        Log.e(TAG, "Error generating content", e)
        Result.failure(e)
    }
}
