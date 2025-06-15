package dev.aurakai.auraframefx.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.system.homescreen.HomeScreenTransitionConfig
import dev.aurakai.auraframefx.system.homescreen.HomeScreenTransitionEffect
import dev.aurakai.auraframefx.system.homescreen.HomeScreenTransitionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenTransitionViewModel @Inject constructor(
    private val transitionManager: HomeScreenTransitionManager,
) : ViewModel() {
    private val _currentConfig = MutableStateFlow<HomeScreenTransitionConfig?>(null)
    val currentConfig: StateFlow<HomeScreenTransitionConfig?> = _currentConfig

    init {
        viewModelScope.launch {
            transitionManager.currentConfig.collect { config ->
                _currentConfig.value = config
            }
        }
    }

    fun updateTransitionProperties(properties: Map<String, Any>) {
        viewModelScope.launch {
            val current = _currentConfig.value ?: return@launch
            
            val updatedConfig = when {
                properties.containsKey("defaultOutgoingEffect") -> {
                    val effect = properties["defaultOutgoingEffect"] as? HomeScreenTransitionEffect
                    current.copy(defaultOutgoingEffect = effect ?: current.defaultOutgoingEffect)
                }
                properties.containsKey("defaultIncomingEffect") -> {
                    val effect = properties["defaultIncomingEffect"] as? HomeScreenTransitionEffect
                    current.copy(defaultIncomingEffect = effect ?: current.defaultIncomingEffect)
                }
                else -> current
            }
            
            _currentConfig.update { updatedConfig }
            
            // Save to preferences
            // prefs.putString("home_screen_transition", Json.encodeToString(updatedConfig))
        }
    }

    fun resetToDefault() {
        viewModelScope.launch {
            transitionManager.resetToDefault()
        }
    }
}
