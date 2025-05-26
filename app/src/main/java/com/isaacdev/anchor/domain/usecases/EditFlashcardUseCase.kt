package com.isaacdev.anchor.domain.usecases

import com.isaacdev.anchor.data.repositories.FlashcardRepository
import com.isaacdev.anchor.domain.models.Flashcard
import com.isaacdev.anchor.domain.models.enums.Difficulty
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

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