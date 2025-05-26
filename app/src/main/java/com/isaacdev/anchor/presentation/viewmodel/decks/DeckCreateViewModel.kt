package com.isaacdev.anchor.presentation.viewmodel.decks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isaacdev.anchor.data.repositories.AuthRepository
import com.isaacdev.anchor.domain.models.Deck
import com.isaacdev.anchor.domain.usecases.CreateDeckUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeckCreateViewModel @Inject constructor(
    private val createDeckUseCase: CreateDeckUseCase,
    authRepository: AuthRepository
): ViewModel() {

    val user = authRepository.currentUser

    private val _uiState = MutableStateFlow(DeckCreateUiState())
    val uiState: StateFlow<DeckCreateUiState> = _uiState.asStateFlow()

    /**
     * Creates a new deck with the given title and description.
     *
     * This function launches a coroutine in the viewModelScope to perform the deck creation asynchronously.
     * It updates the UI state to indicate loading, then calls the `createDeckUseCase`.
     *
     * On successful deck creation:
     *  - Updates the UI state with the newly created deck, sets loading to false, and clears any error message.
     *  - Executes the `onSuccess` callback.
     *
     * On failure:
     *  - Updates the UI state by setting loading to false and populating the error message.
     *
     * @param title The title of the deck to be created.
     * @param description The description of the deck to be created.
     * @param onSuccess A callback function to be executed when the deck is successfully created.
     */
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

    /**
     * Clears the error message in the UI state.
     * This is typically called when the user dismisses an error message.
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

data class DeckCreateUiState(
    val deck: Deck? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)