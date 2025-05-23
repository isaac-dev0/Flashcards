package com.isaacdev.anchor.data.manager

import com.isaacdev.anchor.data.model.Flashcard

interface FlashcardManager {
    suspend fun createFlashcard(flashcard: Flashcard): Result<Flashcard>
    suspend fun getFlashcard(id: String, deckId: String): Flashcard?
    suspend fun getFlashcards(deckId: String): List<Flashcard>
    suspend fun editFlashcard(flashcard: Flashcard): Result<Flashcard>
    suspend fun deleteFlashcard(id: String, deckId: String): Result<Unit>
}