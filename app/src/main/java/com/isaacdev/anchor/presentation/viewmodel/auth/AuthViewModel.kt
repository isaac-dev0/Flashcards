package com.isaacdev.anchor.presentation.viewmodel.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isaacdev.anchor.data.repositories.AuthRepository
import com.isaacdev.anchor.data.repositories.implementations.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = combine(authRepository.authState, _uiState) {
        authState, uiState ->
        uiState.copy(authState = authState)
    }.stateFlowIn(viewModelScope, AuthUiState())

    val authState: StateFlow<AuthState> = authRepository.authState

    init {
        viewModelScope.launch {
            authRepository.checkAuthStatus()
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            authRepository.login(email, password)
                .onSuccess { _uiState.update { it.copy(isLoading = false, errorMessage = null) } }
                .onFailure { error ->
                    Log.e("AuthViewModel", "Login failed: ${error.message}")
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }

    fun signup(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            authRepository.signup(email, password)
                .onSuccess { _uiState.update { it.copy(isLoading = false, errorMessage = null) } }
                .onFailure { error ->
                    Log.e("AuthViewModel", "Signup failed: ${error.message}")
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.logout()
                .onSuccess { _uiState.update { it.copy(isLoading = false, errorMessage = null) } }
                .onFailure { error ->
                    Log.e("AuthViewModel", "Sign out failed: ${error.message}")
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

data class AuthUiState(
    val authState: AuthState = AuthState.Loading,
    val isLoading: Boolean = false,
    val errorMessage: String? = ""
)

private fun <T> kotlinx.coroutines.flow.Flow<T>.stateFlowIn(
    scope: kotlinx.coroutines.CoroutineScope,
    initialValue: T
): StateFlow<T> {
    val stateFlow = MutableStateFlow(initialValue)
    scope.launch {
        collect { stateFlow.value = it }
    }
    return stateFlow.asStateFlow()
}