package com.isaacdev.anchor.data.repositories

import com.isaacdev.anchor.domain.models.Flashcard
import kotlinx.coroutines.flow.StateFlow

/**
 * Repository for managing flashcards.
 *
 * This interface defines the contract for interacting with flashcard data.
 * Implementations of this interface will handle the actual data storage and retrieval.
 */
interface FlashcardRepository {
    val flashcards: StateFlow<List<Flashcard>>
    suspend fun createFlashcard(flashcard: Flashcard): Result<Flashcard>
    suspend fun getFlashcard(id: String, deckId: String): Result<Flashcard>
    suspend fun getFlashcards(deckId: String): Result<List<Flashcard>>
    suspend fun editFlashcard(flashcard: Flashcard): Result<Flashcard>
    suspend fun deleteFlashcard(id: String): Result<Unit>
}