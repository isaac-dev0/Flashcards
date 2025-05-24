package com.isaacdev.anchor.data.repositories

import com.isaacdev.anchor.domain.models.Flashcard

interface FlashcardRepository {
    suspend fun createFlashcard(flashcard: Flashcard): Result<Flashcard>
    suspend fun getFlashcard(id: String, deckId: String): Flashcard?
    suspend fun getFlashcards(deckId: String): List<Flashcard>
    suspend fun editFlashcard(flashcard: Flashcard): Result<Flashcard>
    suspend fun deleteFlashcard(id: String, deckId: String): Result<Unit>
}