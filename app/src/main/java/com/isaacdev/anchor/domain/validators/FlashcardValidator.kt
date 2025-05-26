package com.isaacdev.anchor.domain.validators

import com.isaacdev.anchor.domain.exceptions.DeckException
import com.isaacdev.anchor.domain.models.Flashcard
import javax.inject.Inject

/**
 * Validator class for [Flashcard] objects.
 *
 * This class provides methods to validate the properties of a Flashcard,
 * ensuring they meet specific criteria (e.g., not blank, within length limits).
 */
class FlashcardValidator @Inject constructor() {
    fun validateFlashcard(flashcard: Flashcard): Result<Unit> {
        return when {
            flashcard.question.isBlank() -> Result.failure(
                DeckException.ValidationFailed("Flashcard question cannot be empty")
            )
            flashcard.question.length > 100 -> Result.failure(
                DeckException.ValidationFailed("Flashcard question cannot exceed 100 characters")
            )
            flashcard.answer.isBlank() -> Result.failure(
                DeckException.ValidationFailed("Flashcard answer cannot be empty")
            )
            flashcard.answer.length > 100 -> Result.failure(
                DeckException.ValidationFailed("Flashcard answer cannot exceed 100 characters")
            )
            else -> Result.success(Unit)
        }
    }
}