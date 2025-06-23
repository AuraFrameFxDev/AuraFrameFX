package dev.aurakai.auraframefx.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.system.lockscreen.LockScreenCustomizer
import dev.aurakai.auraframefx.system.quicksettings.QuickSettingsCustomizer
import dev.aurakai.auraframefx.system.quicksettings.QuickSettingsConfig
import dev.aurakai.auraframefx.system.quicksettings.QuickSettingsAnimation
import dev.aurakai.auraframefx.system.quicksettings.QuickSettingsTileConfig
import dev.aurakai.auraframefx.system.lockscreen.LockScreenConfig
import dev.aurakai.auraframefx.system.lockscreen.LockScreenElementType
import dev.aurakai.auraframefx.system.lockscreen.LockScreenAnimation
import dev.aurakai.auraframefx.system.overlay.OverlayShape
import dev.aurakai.auraframefx.system.overlay.OverlayImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SystemCustomizationViewModel @Inject constructor(
    private val quickSettingsCustomizer: QuickSettingsCustomizer,
    private val lockScreenCustomizer: LockScreenCustomizer,
) : ViewModel() {
    
    private val _quickSettingsConfig = MutableStateFlow<QuickSettingsConfig?>(null)
    val quickSettingsConfig: StateFlow<QuickSettingsConfig?> = _quickSettingsConfig
    
    private val _lockScreenConfig = MutableStateFlow<LockScreenConfig?>(null)
    val lockScreenConfig: StateFlow<LockScreenConfig?> = _lockScreenConfig
    
    init {
        loadConfigurations()
    }
    
    /**
     * Starts collecting the latest quick settings and lock screen configurations and updates the corresponding state flows.
     *
     * This method launches coroutines to observe configuration changes from the customizer services, ensuring the ViewModel's state flows reflect the current system customization state.
     */
    fun loadConfigurations() {
        viewModelScope.launch {
            quickSettingsCustomizer.currentConfig.collect { config ->
                _quickSettingsConfig.value = config
            }
        }
        
        viewModelScope.launch {
            lockScreenCustomizer.currentConfig.collect { config ->
                _lockScreenConfig.value = config
            }
        }
    }

    fun updateQuickSettingsTileShape(tileId: String, shape: OverlayShape) {
        viewModelScope.launch {
            quickSettingsCustomizer.updateTileShape(tileId, shape)
        }
    }

    fun updateQuickSettingsTileAnimation(tileId: String, animation: QuickSettingsAnimation) {
        viewModelScope.launch {
            quickSettingsCustomizer.updateTileAnimation(tileId, animation)
        }
    }

    fun updateQuickSettingsBackground(image: ImageResource?) {
        viewModelScope.launch {
            quickSettingsCustomizer.updateBackground(image)
        }
    }

    fun updateLockScreenElementShape(elementType: LockScreenElementType, shape: OverlayShape) {
        viewModelScope.launch {
            lockScreenCustomizer.updateElementShape(elementType, shape)
        }
    }

    fun updateLockScreenElementAnimation(
        elementType: LockScreenElementType,
        animation: LockScreenAnimation,
    ) {
        viewModelScope.launch {
            lockScreenCustomizer.updateElementAnimation(elementType, animation)
        }
    }

    fun updateLockScreenBackground(image: ImageResource?) {
        viewModelScope.launch {
            lockScreenCustomizer.updateBackground(image)
        }
    }

    fun resetToDefaults() {
        viewModelScope.launch {
            quickSettingsCustomizer.resetToDefault()
            lockScreenCustomizer.resetToDefault()
        }
    }
}
