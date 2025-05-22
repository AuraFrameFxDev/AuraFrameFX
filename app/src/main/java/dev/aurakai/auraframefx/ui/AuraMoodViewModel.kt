package dev.aurakai.auraframefx.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.aurakai.auraframefx.ui.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class MoodState(
    val mood: AuraMood = AuraMood.Calm,
    val activity: ActivityType = ActivityType.Idle,
)

class AuraMoodViewModel : ViewModel() {

    private val _moodState = MutableStateFlow(MoodState())
    val moodState: StateFlow<MoodState> = _moodState.asStateFlow()

    fun changeMood(newMood: AuraMood) {
        _moodState.update { currentState ->
            currentState.copy(mood = newMood)
        }
    }

    fun onUserInput(input: String) {
        // TODO: React to user input and modify the mood state
        if (input.length > 20) {
            changeMood(AuraMood.Excited)
        } else if (input.length > 10) {
            changeMood(AuraMood.Happy)
        } else {
            changeMood(AuraMood.Calm)
        }
    }
}