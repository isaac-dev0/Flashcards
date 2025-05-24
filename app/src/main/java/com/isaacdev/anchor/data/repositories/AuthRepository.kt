package com.isaacdev.anchor.data.repositories

import com.isaacdev.anchor.data.repositories.implementations.AuthState
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val currentUser: StateFlow<UserInfo?>
    suspend fun checkAuthStatus(): AuthState
    suspend fun signup(email: String, password: String): Result<Unit>
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun logout(): Result<Unit>
}