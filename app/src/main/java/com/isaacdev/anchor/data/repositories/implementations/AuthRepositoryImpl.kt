package com.isaacdev.anchor.data.repositories.implementations

import android.util.Log
import com.isaacdev.anchor.data.modules.IoDispatcher
import com.isaacdev.anchor.data.repositories.AuthRepository
import com.isaacdev.anchor.domain.exceptions.AuthException
import com.isaacdev.anchor.domain.validators.AuthValidator
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.exceptions.RestException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val supabaseClient: io.github.jan.supabase.SupabaseClient,
    private val authValidator: AuthValidator,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
): AuthRepository {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    override val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentUser = MutableStateFlow<UserInfo?>(null)
    override val currentUser: StateFlow<UserInfo?> = _currentUser.asStateFlow()

    private val auth = supabaseClient.auth

    init {
        CoroutineScope(ioDispatcher).launch {
            checkAuthStatus()
        }
    }

    override suspend fun checkAuthStatus(): AuthState = withContext(ioDispatcher) {
        try {
            val session = auth.currentSessionOrNull()
            if (session != null) {
                _currentUser.value = auth.currentUserOrNull()
                _authState.value = AuthState.LoggedIn
            } else {
                _currentUser.value = null
                _authState.value = AuthState.LoggedOut
            }
            _authState.value
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error checking auth status", e)
            _currentUser.value = null
            _authState.value = AuthState.LoggedOut
            _authState.value
        }
    }

    override suspend fun signup(email: String, password: String): Result<Unit> = withContext(ioDispatcher) {
        try {
            _authState.value = AuthState.Loading

            authValidator.validateAuth(email, password).getOrElse {
                _authState.value = AuthState.LoggedOut
                return@withContext Result.failure(it)
            }

            auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }

            _currentUser.value = auth.currentUserOrNull()
            _authState.value = AuthState.LoggedIn

            Result.success(Unit)
        } catch (e: RestException) {
            Log.e("AuthRepository", "Error signing up", e)
            _authState.value = AuthState.LoggedOut
            Result.failure(AuthException.SignupFailed(e.message ?: "Unknown error"))
        } catch (e: Exception) {
            Log.e("AuthRepository", "Unexpected signup error", e)
            _authState.value = AuthState.LoggedOut
            Result.failure(AuthException.SignupFailed(e.message ?: "Unknown error"))
        }
    }

    override suspend fun login(email: String, password: String): Result<Unit> = withContext(ioDispatcher) {
        try {
            _authState.value = AuthState.Loading

            authValidator.validateAuth(email, password).getOrElse {
                _authState.value = AuthState.LoggedOut
                return@withContext Result.failure(it)
            }

            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }

            _currentUser.value = auth.currentUserOrNull()
            _authState.value = AuthState.LoggedIn
            Result.success(Unit)
        } catch (e: RestException) {
            Log.e("AuthRepository", "Error logging in", e)
            _authState.value = AuthState.LoggedOut
            Result.failure(AuthException.LoginFailed(e.message ?: "Unknown error"))
        } catch (e: Exception) {
            Log.e("AuthRepository", "Unexpected login error", e)
            _authState.value = AuthState.LoggedOut
            Result.failure(AuthException.LoginFailed(e.message ?: "Unknown error"))
        }
    }

    override suspend fun logout(): Result<Unit> = withContext(ioDispatcher) {
        try {
            _authState.value = AuthState.Loading
            auth.signOut()
            _currentUser.value = null
            _authState.value = AuthState.LoggedOut
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error logging out", e)
            _currentUser.value = null
            _authState.value = AuthState.LoggedOut
            Result.failure(AuthException.LogoutFailed(e.message ?: "Unknown error"))
        }
    }
}

sealed class AuthState {
    data object Loading: AuthState()
    data object LoggedIn: AuthState()
    data object LoggedOut: AuthState()
}