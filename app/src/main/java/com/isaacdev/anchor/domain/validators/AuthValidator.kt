package com.isaacdev.anchor.domain.validators

import com.isaacdev.anchor.domain.exceptions.AuthException
import javax.inject.Inject

/**
 * Validates authentication credentials.
 *
 * This class provides methods to validate email and password inputs
 * for authentication purposes.
 */
class AuthValidator @Inject constructor() {
    fun validateAuth(email: String, password: String): Result<Unit> {
        return when {
            email.isBlank() -> Result.failure(
                AuthException.ValidationFailed("Email cannot be empty")
            )
            !email.isEmailValid() -> Result.failure(
                AuthException.ValidationFailed("Invalid email format")
            )
            password.isBlank() -> Result.failure(
                AuthException.ValidationFailed("Password cannot be empty")
            )
            else -> Result.success(Unit)
        }
    }
}

/**
 * Checks if the string is a valid email address.
 *
 * This function uses a regular expression to validate the email format.
 * The email must start with a letter, followed by any characters, an "@" symbol,
 * at least one character, a ".", and at least one character.
 *
 * @return `true` if the string is a valid email, `false` otherwise.
 */
fun String.isEmailValid(): Boolean {
    val emailRegex = Regex("^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})")
    return emailRegex.matches(this)
}