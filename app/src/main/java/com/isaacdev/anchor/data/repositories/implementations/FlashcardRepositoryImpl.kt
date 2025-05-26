package com.isaacdev.anchor.data.repositories.implementations

import android.util.Log
import com.isaacdev.anchor.data.modules.IoDispatcher
import com.isaacdev.anchor.data.repositories.FlashcardRepository
import com.isaacdev.anchor.domain.exceptions.FlashcardException
import com.isaacdev.anchor.domain.models.Flashcard
import com.isaacdev.anchor.domain.validators.FlashcardValidator
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Implementation of [FlashcardRepository] that uses Supabase as the backend.
 *
 * This class provides methods to create, retrieve, update, and delete flashcards.
 * It also exposes a [StateFlow] of the current list of flashcards, which can be observed
 * by UI components to react to changes in the data.
 *
 * All database operations are performed on an I/O dispatcher to avoid blocking the main thread.
 * Input validation is performed using a [FlashcardValidator] before any database operation.
 *
 * @property supabaseClient The Supabase client instance for interacting with the database.
 * @property flashcardValidator The validator for flashcard data.
 * @property ioDispatcher The coroutine dispatcher for I/O operations.
 */
class FlashcardRepositoryImpl @Inject constructor(
    private val supabaseClient: io.github.jan.supabase.SupabaseClient,
    private val flashcardValidator: FlashcardValidator,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
): FlashcardRepository {

    private val _flashcards = MutableStateFlow<List<Flashcard>>(emptyList())
    override val flashcards: StateFlow<List<Flashcard>> = _flashcards.asStateFlow()

    private val table = "flashcards"

    private val database = supabaseClient

    /**
     * Creates a new flashcard.
     *
     * This function validates the provided flashcard data, inserts it into the database,
     * and then updates the local list of flashcards.
     *
     * @param flashcard The [Flashcard] object to be created.
     * @return A [Result] object containing the created [Flashcard] on success,
     * or a [FlashcardException] on failure.
     * Possible failures include validation errors (e.g., empty front or back)
     * or database insertion errors.
     */
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

    /**
     * Retrieves a specific flashcard by its ID and deck ID.
     *
     * @param id The ID of the flashcard to retrieve.
     * @param deckId The ID of the deck the flashcard belongs to.
     * @return A [Result] containing the [Flashcard] if found, or a [FlashcardException] if an error occurs
     * (e.g., ID is blank, flashcard not found).
     */
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

    /**
     * Retrieves all flashcards associated with a specific deck.
     *
     * This function queries the database for all flashcards that have a `deck_id` matching the provided [deckId].
     * Upon successful retrieval, it updates the internal `_flashcards` state flow with the fetched list
     * and returns a [Result.success] containing the list of [Flashcard] objects.
     *
     * If any error occurs during the database operation (e.g., network issue, database error),
     * it logs the error and returns a [Result.failure] with a [FlashcardException.FetchFailed].
     *
     * @param deckId The ID of the deck for which to retrieve flashcards.
     * @return A [Result] object which is either:
     *         - [Result.success] containing a list of [Flashcard] objects if the operation was successful.
     *         - [Result.failure] containing a [FlashcardException.FetchFailed] if an error occurred.
     */
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

    /**
     * Edits an existing flashcard in the database.
     *
     * This function first validates the provided flashcard using [flashcardValidator].
     * If validation fails, it returns a [Result.failure] with the validation error.
     * Otherwise, it attempts to update the flashcard in the Supabase database.
     * If the update is successful and the flashcard is found, the local cache `_flashcards` is updated.
     *
     * @param flashcard The [Flashcard] object to be edited. It must contain a valid ID.
     * @return A [Result] object containing the updated [Flashcard] on success,
     *         or a [FlashcardException.UpdateFailed] on failure (e.g., flashcard not found, database error, validation error).
     */
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

    /**
     * Deletes a flashcard from the database.
     *
     * @param id The ID of the flashcard to delete.
     * @return A [Result] indicating success (with [Unit]) or failure (with a [FlashcardException]).
     * Possible exceptions:
     * - [FlashcardException.InvalidId] if the provided ID is blank.
     * - [FlashcardException.DeletionFailed] if an error occurs during the deletion process.
     */
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