package com.isaacdev.anchor.domain.usecases

import com.isaacdev.anchor.data.repositories.AuthRepository
import com.isaacdev.anchor.data.repositories.FlashcardRepository
import com.isaacdev.anchor.domain.exceptions.FlashcardException
import com.isaacdev.anchor.domain.models.Flashcard
import com.isaacdev.anchor.domain.models.enums.Difficulty
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for creating a new flashcard.
 *
 * This class encapsulates the business logic required to create a flashcard.
 * It interacts with the [FlashcardRepository] to persist the flashcard data
 * and the [AuthRepository] to ensure the user is authenticated.
 *
 * @property flashcardRepository The repository for flashcard data operations.
 * @property authRepository The repository for authentication operations.
 */
@Singleton
class CreateFlashcardUseCase @Inject constructor(
    private val flashcardRepository: FlashcardRepository,
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(deckId: String, question: String, answer: String, difficulty: Difficulty): Result<Flashcard> {
        val currentUser = authRepository.currentUser.value
            ?: return Result.failure(FlashcardException.ValidationFailed("User not authenticated"))

        val flashcard = Flashcard(
            id = UUID.randomUUID().toString(),
            deckId = deckId,
            question = question.trim(),
            answer = answer.trim(),
            difficulty = difficulty,
            createdAt = LocalDateTime.now().toString()
        )

        return flashcardRepository.createFlashcard(flashcard)
    }
}