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

/**
 * ViewModel responsible for managing the authentication state and business logic
 * for user authentication within the application.
 *
 * It interacts with the [AuthRepository] to perform authentication operations like
 * login, signup, and logout. It exposes the authentication state and UI-related
 * state (loading indicators, error messages) as [StateFlow]s to be observed by the UI.
 *
 * The [uiState] is a combination of the raw authentication state from the repository
 * and additional UI-specific states managed by this ViewModel.
 *
 * @property authRepository The repository responsible for handling authentication data and operations.
 */
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

    /**
     * Attempts to log in a user with the provided email and password.
     *
     * This function initiates an asynchronous login process.
     * It updates the UI state to indicate loading, then calls the `authRepository` to perform the login.
     * On successful login, it updates the UI state to reflect success and clears any previous error messages.
     * On failure, it logs the error and updates the UI state with the error message.
     *
     * @param email The user's email address.
     * @param password The user's password.
     */
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

    /**
     * Attempts to sign up a new user with the provided email and password.
     *
     * This function updates the UI state to indicate loading and clears any previous error messages.
     * It then calls the `signup` method of the `authRepository`.
     * On successful signup, the UI state is updated to reflect that loading is complete and
     * any error messages are cleared.
     * On failure, an error message is logged, and the UI state is updated to show the error
     * message and indicate that loading is complete.
     *
     * @param email The email address of the user to sign up.
     * @param password The password for the new user account.
     */
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

    /**
     * Signs out the current user.
     *
     * This function calls the `logout` method from the `authRepository` to sign out the user.
     * On successful logout, it updates the UI state to reflect that the loading process is complete and there are no error messages.
     * On failure, it logs the error and updates the UI state to reflect that the loading process is complete and display an error message.
     */
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

    /**
     * Clears the error message in the UI state.
     * This function is typically called when the user has acknowledged an error
     * and the error message should no longer be displayed.
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

data class AuthUiState(
    val authState: AuthState = AuthState.Loading,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

/**
 * Converts a Flow into a StateFlow.
 *
 * This extension function allows for the collection of a Flow within a given CoroutineScope
 * and emits its values to a StateFlow. The StateFlow is initialized with an initial value.
 *
 * @param T The type of elements emitted by the Flow.
 * @param scope The CoroutineScope in which the Flow will be collected.
 * @param initialValue The initial value for the StateFlow.
 * @return A StateFlow that emits values from the original Flow.
 */
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