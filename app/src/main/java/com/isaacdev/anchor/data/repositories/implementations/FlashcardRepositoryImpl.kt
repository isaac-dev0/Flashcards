package com.isaacdev.anchor.data.repositories.implementations

import android.util.Log
import com.isaacdev.anchor.data.database.SupabaseClient
import com.isaacdev.anchor.data.repositories.FlashcardRepository
import com.isaacdev.anchor.domain.models.Flashcard
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Count
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FlashcardRepositoryImpl: FlashcardRepository {

    private val _flashcards = MutableStateFlow<List<Flashcard>>(emptyList())
    val flashcards: StateFlow<List<Flashcard>> = _flashcards.asStateFlow()

    private val supabase = SupabaseClient.client
    private val table = "flashcards"

    override suspend fun createFlashcard(flashcard: Flashcard): Result<Flashcard> {
        return try {
            val response = supabase.from(table)
                .insert(flashcard) { select() }
                .decodeSingle<Flashcard>()
            _flashcards.value += response
            Result.success(response)
        } catch (e: Exception) {
            Log.e("FlashcardRepository", "Error creating flashcard: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun getFlashcard(id: String, deckId: String): Flashcard? {
        return try {
            supabase.from(table)
                .select { filter {
                    eq("id", id)
                    eq("deck_id", deckId)
                } }
                .decodeSingle<Flashcard>()
        } catch (e: Exception) {
            Log.e("FlashcardRepository", "Error getting flashcard: ${e.message}")
            null
        }
    }

    override suspend fun getFlashcards(deckId: String): List<Flashcard> {
        return try {
            val response = supabase.from(table)
                .select { filter { eq("deck_id", deckId) } }
                .decodeList<Flashcard>()
            _flashcards.value = response
            response
        } catch (e: Exception) {
            Log.e("FlashcardRepository", "Error getting flashcards: ${e.message}")
            emptyList()
        }
    }

    override suspend fun editFlashcard(flashcard: Flashcard): Result<Flashcard> {
        return try {
            val response = supabase.from(table)
                .update(flashcard) {
                    filter { eq("id", flashcard.id) }
                }
                .decodeSingle<Flashcard>()
            _flashcards.value = _flashcards.value.map { if (it.id == flashcard.id) response else it }
            Result.success(response)
        } catch (e: Exception) {
            Log.e("FlashcardRepository", "Error editing flashcard: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun deleteFlashcard(id: String, deckId: String): Result<Unit> {
        return try {
            supabase.from(table)
                .delete { filter {
                    eq("id", id)
                    eq("deck_id", deckId)
                } }
            _flashcards.value = _flashcards.value.filter { it.id != id }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FlashcardRepository", "Error deleting flashcard: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun countFlashcards(deckId: String): Int {
        return try {
            val count = supabase.from(table)
                .select {
                    filter { eq("deck_id", deckId) }
                    count(Count.EXACT)
                }
                .countOrNull()
            count?.toInt() ?: 0
        } catch (e: Exception) {
            Log.e("FlashcardRepository", "Error counting flashcards: ${e.message}")
            0
        }
    }
}