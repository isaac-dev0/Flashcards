package com.isaacdev.anchor.presentation.viewmodel.decks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isaacdev.anchor.data.repositories.DeckRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeckListViewModel @Inject constructor(
    private val deckRepository: DeckRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(DeckListUiState())
    val uiState: StateFlow<DeckListUiState> = _uiState.asStateFlow()

    val decks = deckRepository.decks

    init {
        loadDecks()
    }

    fun loadDecks() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            deckRepository.getDecks()
                .onSuccess { _uiState.update { it.copy(isLoading = false, errorMessage = null) } }
                .onFailure { error -> _uiState.update { it.copy(isLoading = false, errorMessage = error.message) } }
        }
    }

    fun deleteDeck(deckId: String) {
        viewModelScope.launch {
            deckRepository.deleteDeck(deckId)
                .onFailure { error -> _uiState.update { it.copy(errorMessage = error.message) } }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

data class DeckListUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = ""
)