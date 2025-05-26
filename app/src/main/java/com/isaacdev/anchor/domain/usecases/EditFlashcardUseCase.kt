package com.isaacdev.anchor.domain.usecases

import com.isaacdev.anchor.data.repositories.FlashcardRepository
import com.isaacdev.anchor.domain.models.Flashcard
import com.isaacdev.anchor.domain.models.enums.Difficulty
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for editing an existing flashcard.
 *
 * This class encapsulates the logic for modifying a flashcard's details.
 * It takes the flashcard's ID, the ID of the deck it belongs to,
 * the updated question, answer, and difficulty level as input.
 * It then constructs a new `Flashcard` object with these updated details
 * and uses the `FlashcardRepository` to persist the changes.
 * The `createdAt` timestamp is updated to the current time when the flashcard is edited.
 *
 * @property flashcardRepository The repository responsible for flashcard data operations.
 */
@Singleton
class EditFlashcardUseCase @Inject constructor(
    private val flashcardRepository: FlashcardRepository
) {
    suspend operator fun invoke(id: String, deckId: String, question: String, answer: String, difficulty: Difficulty): Result<Flashcard> {
        val flashcard = Flashcard(
            id = id,
            deckId = deckId,
            question = question.trim(),
            answer = answer.trim(),
            difficulty = difficulty,
            createdAt = LocalDateTime.now().toString()
        )

        return flashcardRepository.editFlashcard(flashcard)
    }
}