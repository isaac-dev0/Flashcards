package com.isaacdev.anchor.presentation.viewmodel.flashcards

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isaacdev.anchor.data.repositories.FlashcardRepository
import com.isaacdev.anchor.domain.models.Flashcard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FlashcardReviewViewModel @Inject constructor(
    private val flashcardRepository: FlashcardRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(FlashcardUiState())
    val uiState: StateFlow<FlashcardUiState> = _uiState.asStateFlow()

    private val reviewQueue = mutableListOf<Flashcard>()
    private val retryQueue = mutableListOf<Flashcard>()

    private val _currentCard = MutableStateFlow<Flashcard?>(null)
    val currentCard: StateFlow<Flashcard?> = _currentCard.asStateFlow()

    private val _isReviewFinished = MutableStateFlow(false)
    val isReviewFinished: StateFlow<Boolean> = _isReviewFinished.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun startReview(deckId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            Log.d("FlashcardReviewVM", "startReview - Setting isLoading to true: ${_uiState.value.isLoading}")
            val result = flashcardRepository.getFlashcards(deckId)
            Log.d("FlashcardReviewVM", "startReview: $result")
            result.onSuccess { cards ->
                reviewQueue.clear()
                retryQueue.clear()
                reviewQueue.addAll(cards)
                _uiState.update { it.copy(isLoading = false) }
                Log.d("FlashcardReviewVM", "startReview - Setting isLoading to false: ${_uiState.value.isLoading}, Cards in queue: ${reviewQueue.size}")
                showNextCard()
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                Log.e("FlashcardReviewVM", "Error fetching flashcards: ${error.message}")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun markCorrect() {
        showNextCard()
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun markIncorrect() {
        _currentCard.value?.let { retryQueue.add(it) }
        showNextCard()
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    private fun showNextCard() {
        Log.d("FlashcardReviewVM", "showNextCard called. Review queue size: ${reviewQueue.size}, Retry queue size: ${retryQueue.size}")
        if (reviewQueue.isNotEmpty()) {
            val next = reviewQueue.removeFirst()
            _currentCard.value = next
            Log.d("FlashcardReviewVM", "showNextCard - Displaying card: $next")
        } else if (retryQueue.isNotEmpty()) {
            reviewQueue.addAll(retryQueue)
            retryQueue.clear()
            val next = reviewQueue.removeFirst()
            _currentCard.value = next
            Log.d("FlashcardReviewVM", "showNextCard - Displaying retry card: $next")
        } else {
            _currentCard.value = null
            _isReviewFinished.value = true
            Log.d("FlashcardReviewVM", "showNextCard - Review finished. isReviewFinished: ${_isReviewFinished.value}")
        }
        Log.d("FlashcardReviewVM", "showNextCard - currentCard: ${_currentCard.value}")
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}