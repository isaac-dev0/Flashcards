package com.isaacdev.anchor.presentation.viewmodel.flashcards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isaacdev.anchor.data.repositories.AuthRepository
import com.isaacdev.anchor.domain.models.Flashcard
import com.isaacdev.anchor.domain.models.enums.Difficulty
import com.isaacdev.anchor.domain.usecases.CreateFlashcardUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FlashcardCreateViewModel @Inject constructor(
    private val createFlashcardUseCase: CreateFlashcardUseCase,
    authRepository: AuthRepository
): ViewModel() {

    val user = authRepository.currentUser

    private val _uiState = MutableStateFlow(FlashcardCreateUiState())
    val uiState: StateFlow<FlashcardCreateUiState> = _uiState.asStateFlow()

    fun createFlashcard(deckId: String, question: String, answer: String, difficulty: Difficulty, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            createFlashcardUseCase(deckId, question, answer, difficulty)
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
    val errorMessage: String? = null
)