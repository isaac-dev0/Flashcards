package com.isaacdev.anchor.presentation.viewmodel.flashcards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isaacdev.anchor.data.repositories.FlashcardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FlashcardListViewModel @Inject constructor(
    private val flashcardRepository: FlashcardRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(FlashcardListUiState())
    val uiState: StateFlow<FlashcardListUiState> = _uiState.asStateFlow()

    val flashcards = flashcardRepository.flashcards

    fun loadFlashcards(deckId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            flashcardRepository.getFlashcards(deckId)
                .onSuccess { _uiState.update { it.copy(isLoading = false, errorMessage = null) } }
                .onFailure { error -> _uiState.update { it.copy(isLoading = false, errorMessage = error.message) } }
        }
    }

    fun deleteFlashcard(id: String) {
        viewModelScope.launch {
            flashcardRepository.deleteFlashcard(id)
                .onFailure { error -> _uiState.update { it.copy(errorMessage = error.message) } }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

data class FlashcardListUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = ""
)