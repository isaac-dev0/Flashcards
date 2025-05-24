package com.isaacdev.anchor.data.repositories

import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val currentUser: StateFlow<UserInfo?>
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun signup(email: String, password: String): Result<Unit>
    suspend fun logout()
}