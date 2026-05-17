package com.soundletter.app.presentation.screens.compose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soundletter.app.domain.repository.LetterRepository
import com.soundletter.app.domain.repository.MusicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SongSuggestion(val title: String, val artist: String)

data class ComposeState(
    val to: String = "",
    val from: String = "",
    val message: String = "",
    val suggestions: List<SongSuggestion> = emptyList(),
    val isAiLoading: Boolean = false
)

class ComposeViewModel(
    private val letterRepository: LetterRepository,
    private val musicRepository: MusicRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ComposeState())
    val state: StateFlow<ComposeState> = _state.asStateFlow()

    fun onToChange(value: String) { _state.value = _state.value.copy(to = value) }
    fun onFromChange(value: String) { _state.value = _state.value.copy(from = value) }
    fun onMessageChange(value: String) { _state.value = _state.value.copy(message = value) }

    fun recommendSongs() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isAiLoading = true)
            // Simulation
            _state.value = _state.value.copy(
                suggestions = listOf(
                    SongSuggestion("Starboy", "The Weeknd"),
                    SongSuggestion("Midnight City", "M83")
                ),
                isAiLoading = false
            )
        }
    }
}
