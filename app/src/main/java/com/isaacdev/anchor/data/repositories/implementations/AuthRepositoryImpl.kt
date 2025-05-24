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
    val currentUser: StateFlow<UserInfo?> = _currentUser.asStateFlow()

    private val authClient = SupabaseClient.client.auth

    init {
        checkLoggedInStatus()
    }

    /**
     * Checks the current logged-in status of the user.
     *
     * This function attempts to retrieve the current session using `authClient.currentSessionOrNull()`.
     * - If a session exists, it means the user is logged in.
     *   - It then updates `_currentUser` with the current user information from `authClient.currentUserOrNull()`.
     *   - It sets `_authState` to `AuthState.LoggedIn`.
     * - If no session exists or if an exception occurs during the process, it means the user is logged out.
     *   - It sets `_authState` to `AuthState.LoggedOut`.
     *
     * This function is typically called during app initialization or when needing to verify
     * the user's authentication state.
     */
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

    /**
     * Signs up a new user with the provided email and password.
     *
     * @param email The email of the user to sign up.
     * @param password The password of the user to sign up.
     * @return A [Result] indicating success (with [Unit]) or failure (with an [Exception]).
     *         On successful signup, the [_authState] is updated to [AuthState.LoggedIn] and
     *         [_currentUser] is updated with the newly signed-up user's information.
     *         Possible exceptions include [RestException] for API-related errors.
     */
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

    /**
     * Logs in a user with the provided email and password.
     *
     * This function attempts to sign in the user using the `authClient`.
     * On successful login:
     *  - The internal `_authState` is updated to `AuthState.LoggedIn`.
     *  - The internal `_currentUser` is updated with the authenticated user's information.
     *  - A `Result.success(Unit)` is returned.
     *
     * On failure:
     *  - If the failure is due to a `RestException` (e.g., incorrect credentials, network error from the auth server),
     *    a `Result.failure(e)` containing the `RestException` is returned.
     *  - If the failure is due to any other `Exception`, a `Result.failure(e)` containing that `Exception` is returned.
     *
     * @param email The email address of the user.
     * @param password The password of the user.
     * @return A `Result<Unit>` indicating success or failure of the login operation.
     *         On success, it's `Result.success(Unit)`.
     *         On failure, it's `Result.failure(Exception)` containing the specific error.
     */
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

    /**
     * Logs out the current user.
     *
     * This function attempts to sign out the user using the `authClient`.
     * Upon successful sign-out, it updates the `_authState` to `AuthState.LoggedOut`
     * and clears the `_currentUser`.
     * If an error occurs during the sign-out process, an error message is logged.
     *
     * @throws Exception if an error occurs during the sign-out process.
     */
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