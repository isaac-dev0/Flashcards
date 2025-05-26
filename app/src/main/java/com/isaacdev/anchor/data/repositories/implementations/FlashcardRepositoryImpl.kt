package com.isaacdev.anchor.data.repositories.implementations

import android.util.Log
import com.isaacdev.anchor.data.database.SupabaseClient
import com.isaacdev.anchor.data.modules.IoDispatcher
import com.isaacdev.anchor.data.repositories.FlashcardRepository
import com.isaacdev.anchor.domain.exceptions.FlashcardException
import com.isaacdev.anchor.domain.models.Flashcard
import com.isaacdev.anchor.domain.validators.FlashcardValidator
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Count
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FlashcardRepositoryImpl @Inject constructor(
    private val supabaseClient: io.github.jan.supabase.SupabaseClient,
    private val flashcardValidator: FlashcardValidator,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
): FlashcardRepository {

    private val _flashcards = MutableStateFlow<List<Flashcard>>(emptyList())
    override val flashcards: StateFlow<List<Flashcard>> = _flashcards.asStateFlow()

    private val table = "flashcards"

    private val database = supabaseClient

    override suspend fun createFlashcard(flashcard: Flashcard): Result<Flashcard> = withContext(ioDispatcher) {
        try {
            flashcardValidator.validateFlashcard(flashcard).getOrElse { error ->
                return@withContext Result.failure(error)
            }
            val response = database.from(table)
                .insert(flashcard) { select() }
                .decodeSingle<Flashcard>()
            _flashcards.update { currentFlashcards -> currentFlashcards + response }
            Result.success(response)
        } catch (e: Exception) {
            Log.e("FlashcardRepository", "Error creating flashcard", e)
            Result.failure(FlashcardException.CreationFailed(e.message ?: "Unknown error"))
        }
    }

    override suspend fun getFlashcard(id: String, deckId: String): Result<Flashcard> = withContext(ioDispatcher) {
        try {
            if (id.isBlank()) {
                return@withContext Result.failure(FlashcardException.InvalidId("Flashcard ID cannot be empty."))
            }
            val flashcard = database.from(table)
                .select { filter {
                    eq("id", id)
                    eq("deck_id", deckId)
                } }
                .decodeSingle<Flashcard>()
            Result.success(flashcard)
        } catch (e: Exception) {
            Log.e("FlashcardRepository", "Error getting flashcard with ID: $id", e)
            Result.failure(FlashcardException.NotFound("Flashcard not found"))
        }
    }

    override suspend fun getFlashcards(deckId: String): Result<List<Flashcard>> = withContext(ioDispatcher) {
        try {
            val response = database.from(table)
                .select { filter { eq("deck_id", deckId) } }
                .decodeList<Flashcard>()
            _flashcards.value = response
            Result.success(response)
        } catch (e: Exception) {
            Log.e("FlashcardRepository", "Error getting flashcards", e)
            Result.failure(FlashcardException.FetchFailed(e.message ?: "Unknown error"))
        }
    }

    override suspend fun editFlashcard(flashcard: Flashcard): Result<Flashcard> = withContext(ioDispatcher) {
        try {
            flashcardValidator.validateFlashcard(flashcard).getOrElse { error ->
                return@withContext Result.failure(error)
            }
            val response = database.from(table)
                .update(flashcard) {
                    filter { eq("id", flashcard.id) }
                    select()
                }
                .decodeSingleOrNull<Flashcard>()

            if (response == null) {
                return@withContext Result.failure(FlashcardException.UpdateFailed("Flashcard not found"))
            }

            _flashcards.update { currentFlashcards ->
                currentFlashcards.map { if (it.id == flashcard.id) response else it }
            }
            Result.success(response)
        } catch (e: Exception) {
            Log.e("FlashcardRepository", "Error editing flashcard", e)
            Result.failure(FlashcardException.UpdateFailed(e.message ?: "Unknown error"))
        }
    }

    override suspend fun deleteFlashcard(id: String): Result<Unit> = withContext(ioDispatcher) {
        try {
            if (id.isBlank()) {
                return@withContext Result.failure(FlashcardException.InvalidId("Flashcard ID cannot be empty."))
            }

            database.from(table)
                .delete { filter { eq("id", id) } }

            _flashcards.update { currentFlashcards -> currentFlashcards.filter { it.id != id } }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FlashcardRepository", "Error deleting flashcard", e)
            Result.failure(FlashcardException.DeletionFailed(e.message ?: "Unknown error"))
        }
    }
}