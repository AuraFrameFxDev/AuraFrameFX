package dev.aurakai.auraframefx.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import dev.aurakai.auraframefx.data.Overlay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.io.File

class OverlayManager(private val context: Context) {
    private val _overlays = MutableStateFlow<List<Overlay>>(emptyList())
    val overlays: StateFlow<List<Overlay>> = _overlays

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences("overlay_prefs", Context.MODE_PRIVATE)
    }

    private val overlayDir: File by lazy {
        File(context.filesDir, "overlays").apply { mkdirs() }
    }

    init {
        loadOverlays()
    }

    fun createOverlay(type: Overlay.Type, image: Bitmap? = null): Overlay {
        val overlay = Overlay.createDefaultOverlay(type).copy(image = image)
        saveOverlay(overlay)
        return overlay
    }

    fun updateOverlay(overlay: Overlay) {
        saveOverlay(overlay)
    }

    fun deleteOverlay(overlay: Overlay) {
        val overlayFile = File(overlayDir, "${overlay.id}.png")
        overlayFile.delete()

        _overlays.update { it.filter { o -> o.id != overlay.id } }
        saveOverlayList()
    }

    fun loadImageForOverlay(overlay: Overlay): Bitmap? {
        val overlayFile = File(overlayDir, "${overlay.id}.png")
        return if (overlayFile.exists()) {
            BitmapFactory.decodeFile(overlayFile.absolutePath)
        } else {
            null
        }
    }

    fun saveImageForOverlay(overlay: Overlay, image: Bitmap) {
        val overlayFile = File(overlayDir, "${overlay.id}.png")
        image.compress(Bitmap.CompressFormat.PNG, 100, overlayFile.outputStream())
    }

    private fun saveOverlay(overlay: Overlay) {
        _overlays.update { it.filter { o -> o.id != overlay.id } + overlay }
        saveOverlayList()
    }

    private fun saveOverlayList() {
        prefs.edit().putString(
            "overlays",
            _overlays.value.joinToString(separator = "|") { it.id }
        ).apply()
    }

    private fun loadOverlays() {
        val overlayIds = prefs.getString("overlays", "")?.split("|") ?: emptyList()
        val overlays = overlayIds.mapNotNull { id ->
            val overlayFile = File(overlayDir, "${id}.png")
            val image = if (overlayFile.exists()) {
                BitmapFactory.decodeFile(overlayFile.absolutePath)
            } else {
                null
            }

            // Load overlay properties from shared preferences
            val type = prefs.getString("${id}_type", "")?.let { Overlay.Type.valueOf(it) }
            val name = prefs.getString("${id}_name", "")
            val positionX = prefs.getFloat("${id}_pos_x", 0f)
            val positionY = prefs.getFloat("${id}_pos_y", 0f)
            val sizeWidth = prefs.getFloat("${id}_size_w", 0f)
            val sizeHeight = prefs.getFloat("${id}_size_h", 0f)
            val zIndex = prefs.getFloat("${id}_z", 0f)
            val isDraggable = prefs.getBoolean("${id}_draggable", true)
            val isResizable = prefs.getBoolean("${id}_resizable", true)
            val isRotatable = prefs.getBoolean("${id}_rotatable", true)
            val isLocked = prefs.getBoolean("${id}_locked", false)
            val color = prefs.getString("${id}_color", null)
            val opacity = prefs.getFloat("${id}_opacity", 1f)
            val rotation = prefs.getFloat("${id}_rotation", 0f)

            if (type != null && name != null) {
                Overlay(
                    id = id,
                    name = name,
                    type = type,
                    image = image,
                    position = DpOffset(positionX.dp, positionY.dp),
                    size = Size(sizeWidth, sizeHeight),
                    zIndex = zIndex,
                    isDraggable = isDraggable,
                    isResizable = isResizable,
                    isRotatable = isRotatable,
                    isLocked = isLocked,
                    color = color,
                    opacity = opacity,
                    rotation = rotation
                )
            } else {
                null
            }
        }

        _overlays.value = overlays
    }

    fun restartSystemUI() {
        try {
            val intent = Intent("android.intent.action.RESTART")
            intent.setClassName("com.android.systemui", "com.android.systemui.SystemUIService")
            context.startService(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun clearAppCache() {
        try {
            val cacheDir = context.cacheDir
            val files = cacheDir.listFiles()
            files?.forEach { it.delete() }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun emergencyDisableAll() {
        _overlays.update { emptyList() }
        saveOverlayList()
        restartSystemUI()
    }
}
