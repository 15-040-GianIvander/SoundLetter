package com.soundletter.app.presentation.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soundletter.app.domain.model.Note
import com.soundletter.app.domain.repository.LetterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HistoryState(
    val historyMessages: List<Note> = emptyList(),
    val isLoading: Boolean = false
)

class HistoryScreenViewModel(
    private val letterRepository: LetterRepository
) : ViewModel() {
    private val _state = MutableStateFlow(HistoryState())
    val state: StateFlow<HistoryState> = _state.asStateFlow()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            letterRepository.getLetters().collect { letters ->
                _state.update { it.copy(historyMessages = letters, isLoading = false) }
            }
        }
    }
}
