package dev.aurakai.auraframefx.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.system.homescreen.HomeScreenTransitionManager
import dev.aurakai.auraframefx.system.homescreen.model.HomeScreenTransitionConfig // Added import
import dev.aurakai.auraframefx.system.homescreen.model.HomeScreenTransitionType // Added import
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
public class HomeScreenTransitionViewModel @Inject constructor(
    private val transitionManager: HomeScreenTransitionManager,
) : ViewModel() {
    private val _currentConfig = MutableStateFlow(HomeScreenTransitionConfig()) // Initialized with default
    public val currentConfig: StateFlow<HomeScreenTransitionConfig?> = _currentConfig // Kept nullable for safety, though now initialized

    init {
        viewModelScope.launch {
            transitionManager.currentConfig.collect { config ->
                _currentConfig.value = config
            }
        }
    }

    public fun updateTransitionType(type: HomeScreenTransitionType) {
        viewModelScope.launch {
            transitionManager.updateTransitionType(type)
        }
    }

    public fun updateTransitionDuration(duration: Int) {
        viewModelScope.launch {
            transitionManager.updateTransitionDuration(duration)
        }
    }

    public fun updateTransitionProperties(properties: Map<String, Any>) {
        viewModelScope.launch {
            transitionManager.updateTransitionProperties(properties)
        }
    }

    public fun resetToDefault() {
        viewModelScope.launch {
            transitionManager.resetToDefault()
        }
    }
}
