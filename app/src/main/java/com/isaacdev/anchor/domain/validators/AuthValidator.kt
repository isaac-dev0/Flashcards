package com.isaacdev.anchor.domain.validators

import com.isaacdev.anchor.domain.exceptions.AuthException
import javax.inject.Inject

class AuthValidator @Inject constructor() {
    fun validateAuth(email: String, password: String): Result<Unit> {
        return when {
            email.isBlank() -> Result.failure(
                AuthException.ValidationFailed("Email cannot be empty")
            )
            email.isEmailValid() -> Result.failure(
                AuthException.ValidationFailed("Invalid email format")
            )
            password.isBlank() -> Result.failure(
                AuthException.ValidationFailed("Password cannot be empty")
            )
            password.isPasswordValid() -> Result.failure(
                AuthException.ValidationFailed("Invalid password format")
            )
            else -> Result.success(Unit)
        }
    }
}

fun String.isEmailValid(): Boolean {
    val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
    return emailRegex.toRegex().matches(this)
}

fun String.isPasswordValid(): Boolean {
    val passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$"
    return passwordRegex.toRegex().matches(this)
}
