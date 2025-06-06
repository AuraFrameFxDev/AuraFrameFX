package com.example.app.ui.models

/**
 * Represents an overlay item.
 * TODO: Define actual properties for the overlay model.
 */
data class Overlay(
    val id: String,
    val type: String? = null, // Example: "image", "text", "webview"
    val content: Any? = null, // Example: URL for image, text content, URL for webview // TODO: Consider using a sealed class for 'content' for better type safety (e.g., TextContent, ImageUrlContent)
    val xPosition: Int = 0,
    val yPosition: Int = 0,
    val width: Int = -1, // -1 for wrap_content or match_parent depending on interpretation
    val height: Int = -1,
    val zOrder: Int = 0,
    // Example for color if 'FFCC' was relevant:
    // val backgroundColor: String? = null // e.g., "#FFCC00"
    // TODO: Review color code 'FFCC' if it was intended for this model.
) {
    // TODO: This class might be unused if OverlayManager and related features are not implemented.
}
