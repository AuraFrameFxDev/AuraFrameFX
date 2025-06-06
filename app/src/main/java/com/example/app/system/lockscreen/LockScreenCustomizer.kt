package com.example.app.system.lockscreen

import com.example.app.system.overlay.*
import com.highcapable.yukihookapi.hook.xposed.prefs.data.YukiHookModulePrefs
import com.highcapable.yukihookapi.hook.xposed.service.YukiHookServiceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LockScreenCustomizer @Inject constructor(
    private val overlayManager: SystemOverlayManager,
    private val shapeManager: ShapeManager,
    private val imageManager: ImageResourceManager,
    private val prefs: YukiHookModulePrefs,
    private val overlayService: YukiHookServiceManager,
) {
    private val _currentConfig = MutableStateFlow<LockScreenConfig?>(null)
    val currentConfig: StateFlow<LockScreenConfig?> = _currentConfig

    private val defaultConfig = LockScreenConfig(
        layout = LockScreenLayout(
            clock = LockScreenClockConfig(
                position = LockScreenPosition.CENTER,
                size = 100,
                style = LockScreenClockStyle.DIGITAL,
                animation = LockScreenAnimation(
                    type = LockScreenAnimationType.PULSE,
                    duration = 1000,
                    easing = "easeInOut"
                )
            ),
            date = LockScreenDateConfig(
                position = LockScreenPosition.BOTTOM_CENTER,
                size = 70,
                style = LockScreenDateStyle.COMPACT,
                animation = LockScreenAnimation(
                    type = LockScreenAnimationType.SLIDE,
                    duration = 500,
                    easing = "easeOut"
                )
            ),
            weather = LockScreenWeatherConfig(
                position = LockScreenPosition.TOP_RIGHT,
                size = 60,
                style = LockScreenWeatherStyle.COMPACT,
                animation = LockScreenAnimation(
                    type = LockScreenAnimationType.ROTATE,
                    duration = 2000,
                    easing = "easeInOut"
                )
            )
        ),
        background = LockScreenBackgroundConfig(
            image = imageManager.getImagesByCategory("Backgrounds").firstOrNull(),
            blur = 20f,
            tint = Color(0x8000FFCC),
            pattern = LockScreenPattern.GRADIENT
        ),
        elements = listOf(
            LockScreenElementConfig(
                type = LockScreenElementType.NOTIFICATIONS,
                position = LockScreenPosition.TOP_LEFT,
                shape = shapeManager.createGlowingButton(),
                animation = LockScreenAnimation(
                    type = LockScreenAnimationType.SCALE,
                    duration = 300,
                    easing = "easeOut"
                )
            ),
            LockScreenElementConfig(
                type = LockScreenElementType.CAMERA,
                position = LockScreenPosition.TOP_RIGHT,
                shape = shapeManager.createRoundedHexagon(),
                animation = LockScreenAnimation(
                    type = LockScreenAnimationType.ROTATE,
                    duration = 500,
                    easing = "easeInOut"
                )
            )
        )
    )

    init {
        loadConfig()
    }

    private fun loadConfig() {
        val savedConfig = prefs.getString("lock_screen_config", null)
        if (savedConfig != null) {
            // TODO: Parse saved config
            _currentConfig.value = defaultConfig
        } else {
            _currentConfig.value = defaultConfig
        }
    }

    fun applyConfig(config: LockScreenConfig) {
        _currentConfig.value = config
        overlayService.hook {
            // TODO: Implement lock screen hooking
        }
    }

    fun resetToDefault() {
        applyConfig(defaultConfig)
    }

    fun updateBackground(image: ImageResource?) {
        val current = _currentConfig.value ?: return
        val newConfig = current.copy(
            background = current.background?.copy(image = image)
        )
        applyConfig(newConfig)
    }

    fun updateElementShape(elementType: LockScreenElementType, shape: OverlayShape) {
        val current = _currentConfig.value ?: return
        val newConfig = current.copy(
            elements = current.elements.map { element ->
                if (element.type == elementType) {
                    element.copy(shape = shape)
                } else element
            }
        )
        applyConfig(newConfig)
    }

    fun updateElementAnimation(elementType: LockScreenElementType, animation: LockScreenAnimation) {
        val current = _currentConfig.value ?: return
        val newConfig = current.copy(
            elements = current.elements.map { element ->
                if (element.type == elementType) {
                    element.copy(animation = animation)
                } else element
            }
        )
        applyConfig(newConfig)
    }
}

@Serializable
data class LockScreenConfig(
    val layout: LockScreenLayout,
    val background: LockScreenBackgroundConfig,
    val elements: List<LockScreenElementConfig>,
)

@Serializable
data class LockScreenLayout(
    val clock: LockScreenClockConfig,
    val date: LockScreenDateConfig,
    val weather: LockScreenWeatherConfig,
)

@Serializable
data class LockScreenClockConfig(
    val position: LockScreenPosition,
    val size: Int,
    val style: LockScreenClockStyle,
    val animation: LockScreenAnimation,
)

@Serializable
data class LockScreenDateConfig(
    val position: LockScreenPosition,
    val size: Int,
    val style: LockScreenDateStyle,
    val animation: LockScreenAnimation,
)

@Serializable
data class LockScreenWeatherConfig(
    val position: LockScreenPosition,
    val size: Int,
    val style: LockScreenWeatherStyle,
    val animation: LockScreenAnimation,
)

@Serializable
data class LockScreenBackgroundConfig(
    val image: ImageResource?,
    val blur: Float,
    val tint: Color,
    val pattern: LockScreenPattern,
)

@Serializable
data class LockScreenElementConfig(
    val type: LockScreenElementType,
    val position: LockScreenPosition,
    val shape: OverlayShape,
    val animation: LockScreenAnimation,
)

@Serializable
data class LockScreenAnimation(
    val type: LockScreenAnimationType,
    val duration: Int,
    val easing: String,
    val delay: Int = 0,
)

enum class LockScreenPosition {
    TOP_LEFT,
    TOP_CENTER,
    TOP_RIGHT,
    CENTER_LEFT,
    CENTER,
    CENTER_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_CENTER,
    BOTTOM_RIGHT
}

enum class LockScreenClockStyle {
    DIGITAL,
    ANALOG,
    MODERN,
    CLASSIC
}

enum class LockScreenDateStyle {
    COMPACT,
    EXPANDED,
    MODERN,
    CLASSIC
}

enum class LockScreenWeatherStyle {
    COMPACT,
    EXPANDED,
    ICON_ONLY,
    TEXT_ONLY
}

enum class LockScreenPattern {
    GRADIENT,
    WAVE,
    GRID,
    NONE
}

enum class LockScreenElementType {
    NOTIFICATIONS,
    CAMERA,
    QUICK_SETTINGS,
    WEATHER,
    DATE,
    CLOCK
}

enum class LockScreenAnimationType {
    PULSE,
    SCALE,
    ROTATE,
    SLIDE,
    WAVE,
    GLOW,
    FADE,
    SWIPE
}
