package dev.aurakai.auraframefx.system.overlay // Ensure this package is correct

import kotlinx.serialization.Serializable

@Serializable
data class ImageResource(
    val id: String,
    val type: String, // e.g., "uri", "drawable_name", "asset_path"
    val source: String, // URI, drawable resource name, or asset file path
    val description: String? = null,
)

// If this file previously contained an ImageResourceManager class or other content,
// it should be preserved. This task only focuses on ensuring the ImageResource data class
// is present and @Serializable. For example:

// class ImageResourceManager @Inject constructor(...) {
//    // ... existing methods ...
// }

// Enums like ImageType, ImageFormat do not need @Serializable as per user instructions.
