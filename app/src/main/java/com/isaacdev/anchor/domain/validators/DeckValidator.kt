package com.isaacdev.anchor.domain.validators

import com.isaacdev.anchor.domain.exceptions.DeckException
import com.isaacdev.anchor.domain.models.Deck
import javax.inject.Inject

/**
 * Validator for Deck objects.
 *
 * This class provides methods to validate the properties of a Deck object,
 * ensuring that they meet the required criteria before being processed or persisted.
 */
class DeckValidator @Inject constructor() {
    fun validateDeck(deck: Deck): Result<Unit> {
        return when {
            deck.title.isBlank() -> Result.failure(
                DeckException.ValidationFailed("Deck title cannot be empty")
            )
            deck.title.length > 100 -> Result.failure(
                DeckException.ValidationFailed("Deck title cannot exceed 100 characters")
            )
            (deck.description?.length ?: 0) > 500 -> Result.failure(
                DeckException.ValidationFailed("Deck description cannot exceed 500 characters")
            )
            deck.userId.isBlank() -> Result.failure(
                DeckException.ValidationFailed("User ID is required")
            )
            else -> Result.success(Unit)
        }
    }
}