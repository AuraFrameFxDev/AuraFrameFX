package dev.aurakai.auraframefx.system.homescreen

import com.highcapable.yukihookapi.hook.xposed.prefs.data.YukiHookModulePrefs
import com.highcapable.yukihookapi.hook.xposed.service.YukiHookServiceManager
import dev.aurakai.auraframefx.system.overlay.SystemOverlayManager // Specific import
import dev.aurakai.auraframefx.system.overlay.ShapeManager // Specific import
import dev.aurakai.auraframefx.system.common.ImageResourceManager // Specific import
import dev.aurakai.auraframefx.system.homescreen.model.HomeScreenTransitionConfig // Added import
import dev.aurakai.auraframefx.system.homescreen.model.HomeScreenTransitionType // Added import
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
public class HomeScreenTransitionManager @Inject constructor(
    private val overlayManager: SystemOverlayManager,
    private val shapeManager: ShapeManager,
    private val imageManager: ImageResourceManager,
    private val prefs: YukiHookModulePrefs,
    private val overlayService: YukiHookServiceManager,
) {
    private val _currentConfig = MutableStateFlow(HomeScreenTransitionConfig()) // Initialize with default
    public val currentConfig: StateFlow<HomeScreenTransitionConfig?> = _currentConfig // Kept nullable for safety

    private val defaultConfig = HomeScreenTransitionConfig(
        type = HomeScreenTransitionType.GLOBE_ROTATE,
        duration = 500,
        // easing = "easeInOut", // Removed, not in HomeScreenTransitionConfig
        properties = mapOf(
            "angle" to 360f,
            "scale" to 1.2f,
            "offset" to 0f,
            "amplitude" to 0.1f,
            "frequency" to 0.5f,
            "color" to "#00FFCC",
            "blur" to 20f,
            "spread" to 0.2f
        )
    )

    init {
        loadConfig()
    }

    private fun loadConfig() {
        public val savedConfig = prefs.getString("home_screen_transition", null)
        if (savedConfig != null) {
            // TODO: Parse saved config
            _currentConfig.value = defaultConfig
        } else {
            _currentConfig.value = defaultConfig
        }
    }

    public fun applyConfig(config: HomeScreenTransitionConfig) {
        _currentConfig.value = config
        overlayService.hook {
            // TODO: Implement transition hooking
        }
    }

    public fun resetToDefault() {
        applyConfig(defaultConfig)
    }

    public fun updateTransitionType(type: HomeScreenTransitionType) {
        public val current = _currentConfig.value ?: return
        public val newConfig = current.copy(
            type = type
        )
        applyConfig(newConfig)
    }

    public fun updateTransitionDuration(duration: Int) {
        public val current = _currentConfig.value ?: return
        public val newConfig = current.copy(
            duration = duration
        )
        applyConfig(newConfig)
    }

    public fun updateTransitionProperties(properties: Map<String, Any>) {
        public val current = _currentConfig.value ?: return
        public val newConfig = current.copy(
            properties = properties
        )
        applyConfig(newConfig)
    }
}
