package com.isaacdev.anchor.presentation.viewmodel.flashcards

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isaacdev.anchor.data.repositories.FlashcardRepository
import com.isaacdev.anchor.domain.models.Flashcard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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

    private val _uiState = MutableStateFlow(FLashcardReviewUiState())
    val uiState: StateFlow<FLashcardReviewUiState> = _uiState.asStateFlow()

    private val reviewQueue = mutableListOf<Flashcard>()
    private val retryQueue = mutableListOf<Flashcard>()

    private val _currentCard = MutableStateFlow<Flashcard?>(null)
    val currentCard: StateFlow<Flashcard?> = _currentCard.asStateFlow()

    private val _isReviewFinished = MutableStateFlow(false)
    val isReviewFinished: StateFlow<Boolean> = _isReviewFinished.asStateFlow()

    private val _remainingTime = MutableStateFlow(30)
    val remainingTime: StateFlow<Int> = _remainingTime.asStateFlow()

    private var timerJob: Job? = null
    private val cardDuration = 30

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun startReview(deckId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = flashcardRepository.getFlashcards(deckId)
            result.onSuccess { cards ->
                reviewQueue.clear()
                retryQueue.clear()
                reviewQueue.addAll(cards)
                _uiState.update { it.copy(isLoading = false) }
                showNextCard()
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                Log.e("FlashcardReviewViewModel", "Error fetching flashcards", error)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun markCorrect() {
        stopTimer()
        showNextCard()
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun markIncorrect() {
        stopTimer()
        _currentCard.value?.let { retryQueue.add(it) }
        showNextCard()
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    private fun showNextCard() {
        stopTimer()
        if (reviewQueue.isNotEmpty()) {
            val next = reviewQueue.removeFirst()
            _currentCard.value = next
            _remainingTime.value = cardDuration
            startTimer()
        } else if (retryQueue.isNotEmpty()) {
            reviewQueue.addAll(retryQueue)
            retryQueue.clear()
            val next = reviewQueue.removeFirst()
            _currentCard.value = next
            _remainingTime.value = cardDuration
            startTimer()
        } else {
            _currentCard.value = null
            _isReviewFinished.value = true
        }
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (_remainingTime.value > 0 && _currentCard.value != null) {
                delay(1000) // millis
                _remainingTime.update { it - 1 }
            }
            if (_remainingTime.value == 0 && _currentCard.value != null) {
                markIncorrect()
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

data class FLashcardReviewUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)