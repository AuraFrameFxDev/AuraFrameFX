package com.example.app.system.overlay

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.highcapable.yukihookapi.hook.xposed.prefs.data.YukiHookModulePrefs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageResourceManager @Inject constructor(
    private val context: Context,
    private val prefs: YukiHookModulePrefs,
) {
    private val imageCache = mutableMapOf<String, ImageResource>()
    private val imageCategories =
        mutableMapOf<String, MutableList<ImageResource>>() // Category -> Images
    private val imageMetadata = mutableMapOf<String, Map<String, Any>>() // ID -> Metadata

    private val _availableImages = MutableStateFlow<List<ImageResource>>(emptyList())
    val availableImages: StateFlow<List<ImageResource>> = _availableImages

    private val _customImages = MutableStateFlow<List<ImageResource>>(emptyList())
    val customImages: StateFlow<List<ImageResource>> = _customImages

    private val imageStorageDir: File by lazy {
        context.getExternalFilesDir("images")?.apply {
            mkdirs()
        } ?: File(context.filesDir, "images").apply {
            mkdirs()
        }
    }

    init {
        loadDefaultCategories()
        loadCustomImages()
    }

    private fun loadDefaultCategories() {
        // Predefined categories
        imageCategories.apply {
            put("Backgrounds", mutableListOf())
            put("QuickSettings", mutableListOf())
            put("LockScreen", mutableListOf())
            put("Notifications", mutableListOf())
            put("Shapes", mutableListOf())
            put("Effects", mutableListOf())
            put("Custom", mutableListOf())
        }
    }

    private fun loadCustomImages() {
        val customImages = mutableListOf<ImageResource>()

        // Load custom images from storage
        imageStorageDir.listFiles()?.forEach { file ->
            if (file.isFile && file.extension.lowercase() in listOf("png", "jpg", "jpeg", "webp")) {
                val id = file.nameWithoutExtension
                val resource = createImageResource(
                    id,
                    file,
                    ImageType.CUSTOM
                )

                // Categorize based on filename convention
                val category = getCategoryFromFilename(id)
                imageCategories[category]?.add(resource)

                customImages.add(resource)
            }
        }

        _customImages.value = customImages
    }

    fun addCustomImage(
        id: String,
        bitmap: Bitmap,
        type: ImageType = ImageType.CUSTOM,
        metadata: Map<String, Any> = emptyMap(),
        category: String? = null,
    ): ImageResource {
        val file = File(imageStorageDir, "$id.png")
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, file.outputStream())

        val resource = createImageResource(
            id,
            file,
            type,
            metadata
        )

        // Add to cache and categories
        imageCache[id] = resource
        imageMetadata[id] = metadata

        val targetCategory = category ?: getCategoryFromFilename(id)
        imageCategories[targetCategory]?.add(resource)

        _customImages.update { current ->
            current + listOf(resource)
        }

        return resource
    }

    fun removeCustomImage(id: String) {
        val file = File(imageStorageDir, "$id.png")
        if (file.exists()) {
            file.delete()
        }

        // Remove from cache and categories
        imageCache.remove(id)
        imageMetadata.remove(id)

        imageCategories.values.forEach { list ->
            list.removeAll { it.id == id }
        }

        _customImages.update { current ->
            current.filter { it.id != id }
        }
    }

    fun getImageById(id: String): ImageResource? {
        return imageCache[id] ?: loadFromStorage(id)
    }

    private fun loadFromStorage(id: String): ImageResource? {
        val file = File(imageStorageDir, "$id.png")
        if (file.exists()) {
            val resource = createImageResource(
                id,
                file,
                ImageType.CUSTOM
            )
            imageCache[id] = resource
            return resource
        }
        return null
    }

    fun getImagesByCategory(category: String): List<ImageResource> {
        return imageCategories[category]?.toList() ?: emptyList()
    }

    fun getAllCategories(): List<String> {
        return imageCategories.keys.toList()
    }

    private fun getCategoryFromFilename(filename: String): String {
        // Simple category detection from filename
        return when {
            filename.contains("bg", ignoreCase = true) -> "Backgrounds"
            filename.contains("qs", ignoreCase = true) -> "QuickSettings"
            filename.contains("lock", ignoreCase = true) -> "LockScreen"
            filename.contains("noti", ignoreCase = true) -> "Notifications"
            filename.contains("shape", ignoreCase = true) -> "Shapes"
            filename.contains("effect", ignoreCase = true) -> "Effects"
            else -> "Custom"
        }
    }

    private fun createImageResource(
        id: String,
        resourceId: Int,
        type: ImageType,
        metadata: Map<String, Any> = emptyMap(),
    ): ImageResource {
        val drawable = ContextCompat.getDrawable(context, resourceId)
        val bitmap = drawable?.toBitmap()
        return ImageResource(
            id = id,
            type = type,
            bitmap = bitmap,
            metadata = metadata
        ).also { resource ->
            imageCache[id] = resource
            imageMetadata[id] = metadata
        }
    }

    private fun createImageResource(
        id: String,
        file: File,
        type: ImageType,
        metadata: Map<String, Any> = emptyMap(),
    ): ImageResource {
        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
        return ImageResource(
            id = id,
            type = type,
            bitmap = bitmap,
            metadata = metadata
        ).also { resource ->
            imageCache[id] = resource
            imageMetadata[id] = metadata
        }
    }

    fun optimizeCache() {
        // Remove unused images from cache
        imageCache.entries.removeIf { entry ->
            // Keep recently accessed images
            false
        }
    }

    fun clearCache() {
        imageCache.clear()
    }
}

@Serializable
data class ImageResource(
    val id: String,
    val type: ImageType,
    val bitmap: Bitmap?,
    val metadata: Map<String, Any> = emptyMap(),
) {
    val width: Int get() = bitmap?.width ?: 0
    val height: Int get() = bitmap?.height ?: 0
    val aspectRatio: Float get() = if (width > 0 && height > 0) width.toFloat() / height else 1f
}

enum class ImageType {
    BACKGROUND,
    ICON,
    ELEMENT,
    CUSTOM,
    SHAPE,
    EFFECT
}

enum class ImageFormat {
    PNG,
    JPG,
    WEBP,
    SVG
}
