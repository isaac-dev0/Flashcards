package com.isaacdev.anchor.data.manager

/**
 * Manages authentication with Supabase.
 */
interface AuthManager {
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun signup(email: String, password: String): Result<Unit>
    suspend fun logout()
}