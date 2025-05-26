package com.isaacdev.anchor.domain.usecases

import com.isaacdev.anchor.data.repositories.AuthRepository
import com.isaacdev.anchor.data.repositories.DeckRepository
import com.isaacdev.anchor.domain.exceptions.DeckException
import com.isaacdev.anchor.domain.models.Deck
import java.time.LocalDateTime
import javax.inject.Inject

class EditDeckUseCase @Inject constructor(
    private val deckRepository: DeckRepository,
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(id: String, title: String, description: String? = null): Result<Deck> {
        val currentUser = authRepository.currentUser.value
            ?: return Result.failure(DeckException.ValidationFailed("User not authenticated"))

        val deck = Deck(
            id = id,
            userId = currentUser.id,
            title = title.trim(),
            description = description?.trim(),
            createdAt = LocalDateTime.now().toString()
        )

        return deckRepository.editDeck(deck)
    }
}