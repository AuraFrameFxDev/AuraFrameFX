package dev.aurakai.auraframefx.generated.api.auraframefxai

import dev.aurakai.auraframefx.generated.model.auraframefxai.GenerateImageDescriptionRequest
import dev.aurakai.auraframefx.generated.model.auraframefxai.GenerateTextRequest

interface ContentApi {
    suspend fun generateText(request: GenerateTextRequest): Any
    suspend fun generateImageDescription(request: GenerateImageDescriptionRequest): Any
}

