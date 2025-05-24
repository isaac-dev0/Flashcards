package com.isaacdev.anchor.presentation.viewmodel.decks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isaacdev.anchor.data.repositories.AuthRepository
import com.isaacdev.anchor.data.repositories.DeckRepository
import com.isaacdev.anchor.domain.models.Deck
import com.isaacdev.anchor.domain.usecases.CreateDeckUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class DeckCreateViewModel @Inject constructor(
    private val createDeckUseCase: CreateDeckUseCase,
    private val deckRepository: DeckRepository,
    authRepository: AuthRepository
): ViewModel() {

    val user = authRepository.currentUser

    private val _uiState = MutableStateFlow(DeckCreateUiState())
    val uiState: StateFlow<DeckCreateUiState> = _uiState.asStateFlow()

    fun loadDeck(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            deckRepository.getDeck(id)
                .onSuccess { deck -> _uiState.update { it.copy(deck = deck, isLoading = false, errorMessage = null) } }
                .onFailure { error -> _uiState.update { it.copy(isLoading = false, errorMessage = error.message) } }
        }
    }

    fun createDeck(title: String, description: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            createDeckUseCase(title, description)
                .onSuccess { deck -> _uiState.update { it.copy(deck = deck, isLoading = false, errorMessage = null) }
                    onSuccess()
                }
                .onFailure { error -> _uiState.update { it.copy(isLoading = false, errorMessage = error.message) } }
        }
    }

    fun editDeck(deck: Deck, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            deckRepository.editDeck(deck)
                .onSuccess { deck -> _uiState.update { it.copy(deck = deck, isLoading = false, errorMessage = null) }
                    onSuccess()
                }
                .onFailure { error -> _uiState.update { it.copy(isLoading = false, errorMessage = error.message) } }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

data class DeckCreateUiState(
    val deck: Deck? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = ""
)