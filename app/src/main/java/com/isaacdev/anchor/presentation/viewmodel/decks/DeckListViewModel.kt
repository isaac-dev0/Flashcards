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

    /**
     * Loads the list of decks from the repository.
     *
     * This function launches a coroutine in the viewModelScope.
     * It first updates the UI state to indicate that loading is in progress and clears any previous error messages.
     * Then, it calls the `getDecks()` method from the `deckRepository`.
     * - On success, it updates the UI state to indicate that loading has finished and clears any error messages.
     * - On failure, it updates the UI state to indicate that loading has finished and sets the error message
     *   to the message from the encountered error.
     */
    fun loadDecks() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            deckRepository.getDecks()
                .onSuccess { _uiState.update { it.copy(isLoading = false, errorMessage = null) } }
                .onFailure { error -> _uiState.update { it.copy(isLoading = false, errorMessage = error.message) } }
        }
    }

    /**
     * Deletes a deck from the repository.
     *
     * This function launches a coroutine in the viewModelScope to perform the delete operation
     * asynchronously. It calls the `deleteDeck` method of the `deckRepository`.
     * If the deletion fails, it updates the `_uiState` with the error message.
     *
     * @param deckId The ID of the deck to be deleted.
     */
    fun deleteDeck(deckId: String) {
        viewModelScope.launch {
            deckRepository.deleteDeck(deckId)
                .onFailure { error -> _uiState.update { it.copy(errorMessage = error.message) } }
        }
    }

    /**
     * Clears the error message in the UI state.
     * This function is typically called when the user has acknowledged an error
     * or when a new operation is initiated that should clear any previous errors.
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

data class DeckListUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)