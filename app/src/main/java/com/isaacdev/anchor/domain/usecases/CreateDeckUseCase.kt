package com.isaacdev.anchor.domain.usecases

import com.isaacdev.anchor.domain.exceptions.DeckException
import com.isaacdev.anchor.data.repositories.AuthRepository
import com.isaacdev.anchor.data.repositories.DeckRepository
import com.isaacdev.anchor.domain.models.Deck
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for creating a new deck.
 *
 * This class handles the business logic of creating a deck, including:
 * - Ensuring a user is authenticated.
 * - Generating a unique ID for the new deck.
 * - Setting the creation timestamp.
 * - Trimming whitespace from the title and description.
 * - Persisting the deck using the [DeckRepository].
 *
 * @property deckRepository The repository for managing deck data.
 * @property authRepository The repository for managing user authentication.
 */
@Singleton
class CreateDeckUseCase @Inject constructor(
    private val deckRepository: DeckRepository,
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(title: String, description: String): Result<Deck> {
        val currentUser = authRepository.currentUser.value
            ?: return Result.failure(DeckException.ValidationFailed("User not authenticated"))

        val deck = Deck(
            id = UUID.randomUUID().toString(),
            title = title.trim(),
            description = description.trim().takeIf { it.isNotEmpty() },
            userId = currentUser.id,
            createdAt = LocalDateTime.now().toString()
        )

        return deckRepository.createDeck(deck)
    }
}