package com.isaacdev.anchor.presentation.viewmodel.flashcards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isaacdev.anchor.data.repositories.AuthRepository
import com.isaacdev.anchor.data.repositories.FlashcardRepository
import com.isaacdev.anchor.domain.models.Flashcard
import com.isaacdev.anchor.domain.models.enums.Difficulty
import com.isaacdev.anchor.domain.usecases.CreateFlashcardUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class FlashcardCreateViewModel @Inject constructor(
    private val createFlashcardUseCase: CreateFlashcardUseCase,
    private val flashcardRepository: FlashcardRepository,
    authRepository: AuthRepository
): ViewModel() {

    val user = authRepository.currentUser

    private val _uiState = MutableStateFlow(FlashcardCreateUiState())
    val uiState: StateFlow<FlashcardCreateUiState> = _uiState.asStateFlow()

    fun loadFlashcard(id: String, deckId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            flashcardRepository.getFlashcard(id, deckId)
                .onSuccess { flashcard -> _uiState.update { it.copy(flashcard = flashcard, isLoading = false, errorMessage = null) } }
                .onFailure { error -> _uiState.update { it.copy(isLoading = false, errorMessage = error.message) } }
        }
    }

    fun createFlashcard(question: String, answer: String, difficulty: Difficulty, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            createFlashcardUseCase(question, answer, difficulty)
                .onSuccess { flashcard -> _uiState.update { it.copy(flashcard = flashcard, isLoading = false, errorMessage = null) }
                    onSuccess()
                }
                .onFailure { error -> _uiState.update { it.copy(isLoading = false, errorMessage = error.message) } }
        }
    }

    fun editFlashcard(flashcard: Flashcard, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            flashcardRepository.editFlashcard(flashcard)
                .onSuccess { flashcard -> _uiState.update { it.copy(flashcard = flashcard, isLoading = false, errorMessage = null) }
                    onSuccess()
                }
                .onFailure { error -> _uiState.update { it.copy(isLoading = false, errorMessage = error.message) } }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

data class FlashcardCreateUiState(
    val flashcard: Flashcard? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = ""
)