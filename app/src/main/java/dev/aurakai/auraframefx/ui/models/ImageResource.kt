package dev.aurakai.auraframefx.ui.models

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ImageResource(
    val uri: String = "",
    val type: ImageType = ImageType.ASSET,
    val width: Int = 0,
    val height: Int = 0,
    val resourceId: Int = 0
) {
    @Serializable
    enum class ImageType {
        ASSET,
        URI,
        RESOURCE_ID,
        BITMAP,
        VECTOR
    }
}
