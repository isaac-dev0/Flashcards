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

/**
 * Implementation of [AuthRepository] that handles user authentication
 * using Supabase.
 *
 * This class manages the authentication state ([authState]) and the currently
 * logged-in user's information ([currentUser]) as [StateFlow]s, allowing
 * other parts of the application to observe changes.
 *
 * It uses an [AuthValidator] to validate user credentials before attempting
 * authentication operations. All network operations are performed on the provided
 * [ioDispatcher] to avoid blocking the main thread.
 *
 * Upon initialization, it immediately checks the current authentication status.
 *
 * @property supabaseClient The Supabase client instance used for authentication.
 * @property authValidator The validator used to check email and password formats.
 * @property ioDispatcher The coroutine dispatcher for performing I/O operations.
 */
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

    /**
     * Checks the current authentication status with the Supabase backend.
     * It attempts to retrieve the current session. If a session exists,
     * the user is considered logged in, and their information is updated.
     * Otherwise, the user is considered logged out.
     *
     * This function updates the internal `_authState` and `_currentUser` StateFlows
     * to reflect the current authentication state.
     *
     * In case of any error during the check (e.g., network issues),
     * it defaults to a logged-out state and logs the error.
     *
     * @return The current [AuthState] (LoggedIn, LoggedOut).
     */
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

    /**
     * Signs up a new user with the provided email and password.
     *
     * This function first validates the email and password using [authValidator].
     * If validation fails, it returns a [Result.failure] with the validation error.
     * Otherwise, it attempts to sign up the user using Supabase authentication.
     *
     * On successful signup:
     * - Updates the [_currentUser] state with the new user information.
     * - Sets the [_authState] to [AuthState.LoggedIn].
     * - Returns [Result.success] with [Unit].
     *
     * On failure (e.g., [RestException] from Supabase or other unexpected errors):
     * - Logs the error.
     * - Sets the [_authState] to [AuthState.LoggedOut].
     * - Returns [Result.failure] with an [AuthException.SignupFailed] containing the error message.
     *
     * This function is executed on the [ioDispatcher] to avoid blocking the main thread.
     *
     * @param email The email address of the user to sign up.
     * @param password The password for the new user account.
     * @return A [Result] indicating success ([Result.success] with [Unit]) or failure ([Result.failure] with an [AuthException]).
     */
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

    /**
     * Logs in a user with the provided email and password.
     *
     * This function attempts to sign in the user using Supabase email authentication.
     * Before attempting to sign in, it validates the email and password using [authValidator].
     * If validation fails, it returns a [Result.failure] with the validation error.
     *
     * On successful login, it updates the [currentUser] and [authState] to [AuthState.LoggedIn].
     * If any [RestException] or other [Exception] occurs during the login process,
     * it logs the error, sets the [authState] to [AuthState.LoggedOut], and returns a
     * [Result.failure] with an [AuthException.LoginFailed].
     *
     * @param email The user's email address.
     * @param password The user's password.
     * @return A [Result] indicating success (with [Unit]) or failure (with an [AuthException]).
     */
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

    /**
     * Logs out the current user.
     *
     * This function signs out the user from Supabase, sets the current user to null,
     * and updates the authentication state to [AuthState.LoggedOut].
     *
     * @return A [Result] indicating success ([Result.success] with [Unit]) or failure ([Result.failure] with an [AuthException.LogoutFailed]).
     */
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

/**
 * Represents the different states of authentication.
 *
 * This sealed class is used to model the possible states of the authentication process,
 * allowing for clear and type-safe handling of different authentication scenarios.
 *
 * - [Loading]: Indicates that an authentication operation is currently in progress.
 * - [LoggedIn]: Indicates that the user is successfully authenticated.
 * - [LoggedOut]: Indicates that the user is not authenticated or has been logged out.
 */
sealed class AuthState {
    data object Loading: AuthState()
    data object LoggedIn: AuthState()
    data object LoggedOut: AuthState()
}