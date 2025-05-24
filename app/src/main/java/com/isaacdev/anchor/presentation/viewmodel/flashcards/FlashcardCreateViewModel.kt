package com.isaacdev.anchor.presentation.viewmodel.flashcards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isaacdev.anchor.domain.models.Flashcard
import com.isaacdev.anchor.data.repositories.implementations.FlashcardRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FlashcardCreateViewModel: ViewModel() {

    private val flashcardRepository = FlashcardRepositoryImpl()

    private val _flashcard = MutableStateFlow<Flashcard?>(null)
    val flashcard: StateFlow<Flashcard?> = _flashcard.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()

    fun loadFlashcard(id: String, deckId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                flashcardRepository.getFlashcard(id, deckId)
                _errorMessage.value = ""
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error loading flashcards: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    suspend fun createFlashcard(flashcard: Flashcard): Result<Flashcard> {
        return try {
            _isLoading.value = true
            val result = flashcardRepository.createFlashcard(flashcard)
            _isLoading.value = false
            if (result.isFailure) {
                _errorMessage.value = "Error creating flashcard: ${result.exceptionOrNull()?.message}"
            }
            result
        } catch (e: Exception) {
            _errorMessage.value = "Error creating flashcard: ${e.message}"
            Result.failure(e)
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun editFlashcard(flashcard: Flashcard): Result<Flashcard> {
        return try {
            _isLoading.value = true
            val result = flashcardRepository.editFlashcard(flashcard)
            _isLoading.value = false
            if (result.isFailure) {
                _errorMessage.value =
                    "Error editing flashcard: ${result.exceptionOrNull()?.message}"
            }
            result
        } catch (e: Exception) {
            _errorMessage.value = "Error editing flashcard: ${e.message}"
            Result.failure(e)
        } finally {
            _isLoading.value = false
        }
    }
}