package com.example.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.system.lockscreen.LockScreenCustomizer
import com.example.app.system.quicksettings.QuickSettingsCustomizer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
        viewModelScope.launch {
            quickSettingsCustomizer.currentConfig.collect { config ->
                _quickSettingsConfig.value = config
            }

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
