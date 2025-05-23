package com.isaacdev.anchor.presentation.viewmodel.decks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isaacdev.anchor.data.model.Deck
import com.isaacdev.anchor.data.repository.AuthRepository
import com.isaacdev.anchor.data.repository.DeckRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DeckCreateViewModel: ViewModel() {

    private val deckRepository = DeckRepository()
    private val authRepository = AuthRepository()

    val user = authRepository.currentUser

    private val _deck = MutableStateFlow<Deck?>(null)
    val deck: StateFlow<Deck?> = _deck.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()

    fun loadDeck(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _deck.value = deckRepository.getDeck(id)
                _errorMessage.value = ""
            } catch (e: Exception) {
                _errorMessage.value = "Error loading deck: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    suspend fun createDeck(deck: Deck): Result<Deck> {
        return try {
            _isLoading.value = true
            val result = deckRepository.createDeck(deck)
            _isLoading.value = false
            if (result.isFailure) {
                _errorMessage.value = "Error creating deck: ${result.exceptionOrNull()?.message}"
            }
            result
        } catch (e: Exception) {
            _errorMessage.value = "Error creating deck: ${e.message}"
            Result.failure(e)
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun editDeck(deck: Deck): Result<Deck> {
        return try {
            _isLoading.value = true
            val result = deckRepository.editDeck(deck)
            _isLoading.value = false
            if (result.isFailure) {
                _errorMessage.value = "Error editing deck: ${result.exceptionOrNull()?.message}"
            }
            result
        } catch (e: Exception) {
            _errorMessage.value = "Error editing deck: ${e.message}"
            Result.failure(e)
        } finally {
            _isLoading.value = false
        }
    }
}