package dev.aurakai.auraframefx.ui.model

import android.graphics.Bitmap

// Using Any for bitmap for now, can be android.graphics.Bitmap or a URL String
public data class ImageResource(
    public val id: String,
    public val type: ImageType,
    public val bitmap: Any, // Could be Bitmap, String URL, etc. AsyncImage handles various types.
    public val path: String? = null // Optional path for local files
)
