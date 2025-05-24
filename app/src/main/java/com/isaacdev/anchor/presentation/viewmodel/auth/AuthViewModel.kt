package com.isaacdev.anchor.presentation.viewmodel.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isaacdev.anchor.data.repositories.implementations.AuthRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepositoryImpl
): ViewModel() {

    val authState = authRepository.authState

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = ""
            try {
                val result = authRepository.login(email, password)
                result.onFailure { e ->
                    _errorMessage.value = e.localizedMessage ?: "Login failed."
                    Log.e("AuthViewModel", "Login failed: ${e.message}")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Login has failed, please try again."
                Log.e("AuthViewModel", "Login failed: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signup(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = ""
            try {
                val result = authRepository.signup(email, password)
                result.onFailure { e ->
                    _errorMessage.value = e.localizedMessage ?: "Signup failed."
                    Log.e("AuthViewModel", "Signup failed: ${e.message}")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Signup has failed, an account with this email address may already exist."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

}