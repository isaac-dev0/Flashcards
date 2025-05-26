package com.isaacdev.anchor.domain.exceptions

/**
 * A sealed class representing authentication-related exceptions.
 * This class provides a common base for various authentication errors that can occur within the application.
 *
 * @param message A descriptive message explaining the reason for the exception.
 * @param cause An optional underlying cause of this exception.
 */
sealed class AuthException(message: String, cause: Throwable? = null): Exception(message, cause) {
    class LoginFailed(message: String): AuthException("Failed to login: $message")
    class SignupFailed(message: String): AuthException("Failed to signup: $message")
    class LogoutFailed(message: String): AuthException("Failed to logout: $message")
    class ValidationFailed(message: String): AuthException(message)
}