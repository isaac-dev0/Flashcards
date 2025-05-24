package com.isaacdev.anchor.data.repositories.implementations

import android.util.Log
import com.isaacdev.anchor.data.database.SupabaseClient
import com.isaacdev.anchor.data.repositories.AuthRepository
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.exceptions.RestException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepositoryImpl: AuthRepository {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentUser = MutableStateFlow<UserInfo?>(null)
    override val currentUser: StateFlow<UserInfo?> = _currentUser.asStateFlow()

    private val authClient = SupabaseClient.client.auth

    init {
        checkLoggedInStatus()
    }

    private fun checkLoggedInStatus() {
        try {
            val session = authClient.currentSessionOrNull()
            if (session != null) {
                _currentUser.value = authClient.currentUserOrNull()
                _authState.value = AuthState.LoggedIn
            } else {
                _authState.value = AuthState.LoggedOut
            }
        } catch (e: Exception) {
            _authState.value = AuthState.LoggedOut
        }
    }

    override suspend fun signup(email: String, password: String): Result<Unit> {
        return try {
            authClient.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            _authState.value = AuthState.LoggedIn
            _currentUser.value = authClient.currentUserOrNull()
            Result.success(Unit)
        } catch (e: RestException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            authClient.signInWith(Email) {
                this.email = email
                this.password = password
            }
            _authState.value = AuthState.LoggedIn
            _currentUser.value = authClient.currentUserOrNull()
            Result.success(Unit)
        } catch (e: RestException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        try {
            authClient.signOut()
            _authState.value = AuthState.LoggedOut
            _currentUser.value = null
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error logging out: ${e.message}")
        }
    }
}

sealed class AuthState {
    data object Loading: AuthState()
    data object LoggedIn: AuthState()
    data object LoggedOut: AuthState()
}