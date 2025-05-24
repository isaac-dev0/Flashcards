package com.isaacdev.anchor.domain.usecases

import com.isaacdev.anchor.domain.exceptions.DeckException
import com.isaacdev.anchor.data.repositories.AuthRepository
import com.isaacdev.anchor.data.repositories.DeckRepository
import com.isaacdev.anchor.domain.models.Deck
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

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