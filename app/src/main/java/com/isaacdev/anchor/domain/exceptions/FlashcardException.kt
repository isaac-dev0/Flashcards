package com.isaacdev.anchor.domain.exceptions

/**
 * This sealed class provides a hierarchy of specific exceptions that can be thrown
 * during flashcard creation, update, deletion, fetching, or when dealing with
 * invalid data or non-existent flashcards.
 *
 * @param message A descriptive message explaining the reason for the exception.
 * @param cause The underlying cause of the exception, if any.
 */
sealed class FlashcardException(message: String, cause: Throwable? = null): Exception(message, cause) {
    class CreationFailed(message: String): FlashcardException("Failed to create flashcard: $message")
    class UpdateFailed(message: String): FlashcardException("Failed to update flashcard: $message")
    class DeletionFailed(message: String): FlashcardException("Failed to delete flashcard: $message")
    class FetchFailed(message: String): FlashcardException("Failed to fetch flashcards: $message")
    class NotFound(message: String): FlashcardException(message)
    class InvalidId(message: String): FlashcardException(message)
    class ValidationFailed(message: String): FlashcardException(message)
}