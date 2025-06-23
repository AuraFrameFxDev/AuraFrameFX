package dev.aurakai.auraframefx.data

class AppStateManager {
    // Minimal placeholder implementation
    var state: String = "default"

    fun updateState(newState: String) {
        state = newState
    }

    fun getState(): String = state
}
