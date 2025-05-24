package com.isaacdev.anchor.presentation.viewmodel.flashcards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isaacdev.anchor.data.repositories.implementations.FlashcardRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FlashcardListViewModel: ViewModel() {

    private val flashcardRepository = FlashcardRepositoryImpl()

    val flashcards = flashcardRepository.flashcards

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()

    fun loadFlashcards(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                flashcardRepository.getFlashcards(id)
                _errorMessage.value = ""
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error loading flashcards: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}