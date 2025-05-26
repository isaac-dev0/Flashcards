package com.isaacdev.anchor.data.repositories

import com.isaacdev.anchor.data.repositories.implementations.AuthState
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.flow.StateFlow

/**
 * Repository interface for handling authentication operations.
 *
 * This interface defines the contract for authentication-related actions such as
 * checking the current authentication status, signing up, logging in, and logging out.
 * It also provides access to the current authentication state and user information
 * as observable flows.
 */
interface AuthRepository {
    val authState: StateFlow<AuthState>
    val currentUser: StateFlow<UserInfo?>
    suspend fun checkAuthStatus(): AuthState
    suspend fun signup(email: String, password: String): Result<Unit>
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun logout(): Result<Unit>
}