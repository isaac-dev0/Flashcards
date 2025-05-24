package com.isaacdev.anchor.domain.exceptions

sealed class AuthException(message: String, cause: Throwable? = null): Exception(message, cause) {
    class LoginFailed(message: String): AuthException("Failed to login: $message")
    class SignupFailed(message: String): AuthException("Failed to signup: $message")
    class LogoutFailed(message: String): AuthException("Failed to logout: $message")
    class ValidationFailed(message: String): AuthException(message)
}