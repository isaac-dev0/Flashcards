package com.isaacdev.anchor.domain.exceptions

/**
 * Represents exceptions that can occur during deck-related operations.
 * This sealed class provides a hierarchy for more specific deck exceptions.
 *
 * @param message A descriptive message explaining the exception.
 * @param cause The underlying cause of the exception, if any.
 */
sealed class DeckException(message: String, cause: Throwable? = null): Exception(message, cause) {
    class CreationFailed(message: String): DeckException("Failed to create deck: $message")
    class UpdateFailed(message: String): DeckException("Failed to update deck: $message")
    class DeletionFailed(message: String): DeckException("Failed to delete deck: $message")
    class FetchFailed(message: String): DeckException("Failed to fetch decks: $message")
    class NotFound(message: String): DeckException(message)
    class InvalidId(message: String): DeckException(message)
    class ValidationFailed(message: String): DeckException(message)
}