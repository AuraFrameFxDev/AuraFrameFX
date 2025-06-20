package dev.aurakai.auraframefx.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.system.lockscreen.LockScreenCustomizer // Added import
import dev.aurakai.auraframefx.system.quicksettings.QuickSettingsCustomizer // Added import
import dev.aurakai.auraframefx.system.quicksettings.model.QuickSettingsConfig // Added import
import dev.aurakai.auraframefx.system.lockscreen.model.LockScreenConfig // Added import
import dev.aurakai.auraframefx.system.overlay.model.OverlayShape // Added import
import dev.aurakai.auraframefx.system.quicksettings.model.QuickSettingsAnimation // Added import
import dev.aurakai.auraframefx.ui.model.ImageResource // Added import
import dev.aurakai.auraframefx.system.lockscreen.model.LockScreenElementType // Added import
import dev.aurakai.auraframefx.system.lockscreen.model.LockScreenAnimation // Added import
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
public class SystemCustomizationViewModel @Inject constructor(
    private val quickSettingsCustomizer: QuickSettingsCustomizer,
    private val lockScreenCustomizer: LockScreenCustomizer,
) : ViewModel() {
    private val _quickSettingsConfig = MutableStateFlow(QuickSettingsConfig()) // Initialized with default
    public val quickSettingsConfig: StateFlow<QuickSettingsConfig?> = _quickSettingsConfig // Kept nullable for safety

    private val _lockScreenConfig = MutableStateFlow(LockScreenConfig()) // Initialized with default
    public val lockScreenConfig: StateFlow<LockScreenConfig?> = _lockScreenConfig // Kept nullable for safety

    init {
        viewModelScope.launch {
            quickSettingsCustomizer.currentConfig.collect { config ->
                _quickSettingsConfig.value = config
            }

            lockScreenCustomizer.currentConfig.collect { config ->
                _lockScreenConfig.value = config
            }
        }
    }

    public fun updateQuickSettingsTileShape(tileId: String, shape: OverlayShape) {
        viewModelScope.launch {
            quickSettingsCustomizer.updateTileShape(tileId, shape)
        }
    }

    public fun updateQuickSettingsTileAnimation(tileId: String, animation: QuickSettingsAnimation) {
        viewModelScope.launch {
            quickSettingsCustomizer.updateTileAnimation(tileId, animation)
        }
    }

    public fun updateQuickSettingsBackground(image: ImageResource?) {
        viewModelScope.launch {
            quickSettingsCustomizer.updateBackground(image)
        }
    }

    public fun updateLockScreenElementShape(elementType: LockScreenElementType, shape: OverlayShape) {
        viewModelScope.launch {
            lockScreenCustomizer.updateElementShape(elementType, shape)
        }
    }

    public fun updateLockScreenElementAnimation(
        elementType: LockScreenElementType,
        animation: LockScreenAnimation,
    ) {
        viewModelScope.launch {
            lockScreenCustomizer.updateElementAnimation(elementType, animation)
        }
    }

    public fun updateLockScreenBackground(image: ImageResource?) {
        viewModelScope.launch {
            lockScreenCustomizer.updateBackground(image)
        }
    }

    public fun resetToDefaults() {
        viewModelScope.launch {
            quickSettingsCustomizer.resetToDefault()
            lockScreenCustomizer.resetToDefault()
        }
    }
}
