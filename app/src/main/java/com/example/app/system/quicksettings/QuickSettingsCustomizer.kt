package com.example.app.system.quicksettings

import com.example.app.system.overlay.*
import com.highcapable.yukihookapi.hook.xposed.prefs.data.YukiHookModulePrefs
import com.highcapable.yukihookapi.hook.xposed.service.YukiHookServiceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuickSettingsCustomizer @Inject constructor(
    private val overlayManager: SystemOverlayManager,
    private val shapeManager: ShapeManager,
    private val imageManager: ImageResourceManager,
    private val prefs: YukiHookModulePrefs,
    private val overlayService: YukiHookServiceManager,
) {
    private val _currentConfig = MutableStateFlow<QuickSettingsConfig?>(null)
    val currentConfig: StateFlow<QuickSettingsConfig?> = _currentConfig

    private val defaultConfig = QuickSettingsConfig(
        layout = QuickSettingsLayout(
            columns = 4,
            rows = 3,
            spacing = 16,
            padding = QuickSettingsPadding(16, 16, 16, 16)
        ),
        tiles = listOf(
            QuickSettingsTileConfig(
                id = "wifi",
                icon = "wifi",
                label = "WiFi",
                shape = shapeManager.createRoundedHexagon(),
                animation = QuickSettingsAnimation(
                    type = QuickSettingsAnimationType.PULSE,
                    duration = 500,
                    easing = "easeInOut"
                )
            ),
            QuickSettingsTileConfig(
                id = "bluetooth",
                icon = "bluetooth",
                label = "Bluetooth",
                shape = shapeManager.createNeonCapsule(),
                animation = QuickSettingsAnimation(
                    type = QuickSettingsAnimationType.SCALE,
                    duration = 300,
                    easing = "easeOut"
                )
            ),
            // Add more default tiles here
        ),
        header = QuickSettingsHeaderConfig(
            shape = shapeManager.createWaveBorder(),
            animation = QuickSettingsAnimation(
                type = QuickSettingsAnimationType.SLIDE,
                duration = 400,
                easing = "easeInOut"
            )
        ),
        background = QuickSettingsBackgroundConfig(
            image = imageManager.getImagesByCategory("Backgrounds").firstOrNull(),
            blur = 10f,
            tint = Color(0x8000FFCC)
        )
    )

    init {
        loadConfig()
    }

    private fun loadConfig() {
        val savedConfig = prefs.getString("quick_settings_config", null)
        if (savedConfig != null) {
            // TODO: Parse saved config
            _currentConfig.value = defaultConfig
        } else {
            _currentConfig.value = defaultConfig
        }
    }

    fun applyConfig(config: QuickSettingsConfig) {
        _currentConfig.value = config
        overlayService.hook {
            // TODO: Implement quick settings hooking
        }
    }

    fun resetToDefault() {
        applyConfig(defaultConfig)
    }

    fun updateTileShape(tileId: String, shape: OverlayShape) {
        val current = _currentConfig.value ?: return
        val newConfig = current.copy(
            tiles = current.tiles.map { tile ->
                if (tile.id == tileId) {
                    tile.copy(shape = shape)
                } else tile
            }
        )
        applyConfig(newConfig)
    }

    fun updateTileAnimation(tileId: String, animation: QuickSettingsAnimation) {
        val current = _currentConfig.value ?: return
        val newConfig = current.copy(
            tiles = current.tiles.map { tile ->
                if (tile.id == tileId) {
                    tile.copy(animation = animation)
                } else tile
            }
        )
        applyConfig(newConfig)
    }

    fun updateBackground(image: ImageResource?) {
        val current = _currentConfig.value ?: return
        val newConfig = current.copy(
            background = current.background?.copy(image = image)
        )
        applyConfig(newConfig)
    }
}

@Serializable
data class QuickSettingsConfig(
    val layout: QuickSettingsLayout,
    val tiles: List<QuickSettingsTileConfig>,
    val header: QuickSettingsHeaderConfig,
    val background: QuickSettingsBackgroundConfig,
)

@Serializable
data class QuickSettingsLayout(
    val columns: Int,
    val rows: Int,
    val spacing: Int,
    val padding: QuickSettingsPadding,
)

@Serializable
data class QuickSettingsPadding(
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int,
)

@Serializable
data class QuickSettingsTileConfig(
    val id: String,
    val icon: String,
    val label: String,
    val shape: OverlayShape,
    val animation: QuickSettingsAnimation,
)

@Serializable
data class QuickSettingsHeaderConfig(
    val shape: OverlayShape,
    val animation: QuickSettingsAnimation,
)

@Serializable
data class QuickSettingsBackgroundConfig(
    val image: ImageResource?,
    val blur: Float,
    val tint: Color,
)

@Serializable
data class QuickSettingsAnimation(
    val type: QuickSettingsAnimationType,
    val duration: Int,
    val easing: String,
    val delay: Int = 0,
)

enum class QuickSettingsAnimationType {
    PULSE,
    SCALE,
    ROTATE,
    SLIDE,
    WAVE,
    GLOW,
    FADE
}
