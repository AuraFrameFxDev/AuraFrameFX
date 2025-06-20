package dev.aurakai.auraframefx.generated.api.auraframefxai

import dev.aurakai.auraframefx.generated.model.auraframefxai.GenerateImageDescriptionRequest
import dev.aurakai.auraframefx.generated.model.auraframefxai.GenerateImageDescriptionResponse // Added import
import dev.aurakai.auraframefx.generated.model.auraframefxai.GenerateTextRequest
import dev.aurakai.auraframefx.generated.model.auraframefxai.GenerateTextResponse // Added import

public interface ContentApi {
    suspend fun generateText(request: GenerateTextRequest): GenerateTextResponse // Corrected return type
    suspend fun generateImageDescription(request: GenerateImageDescriptionRequest): GenerateImageDescriptionResponse // Corrected return type
}

