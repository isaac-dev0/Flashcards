package com.isaacdev.anchor.data.repository

import android.util.Log
import com.isaacdev.anchor.data.SupabaseClient
import com.isaacdev.anchor.data.manager.FlashcardManager
import com.isaacdev.anchor.data.model.Flashcard
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Count
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FlashcardRepository: FlashcardManager {

    private val _flashcards = MutableStateFlow<List<Flashcard>>(emptyList())
    val flashcards: StateFlow<List<Flashcard>> = _flashcards.asStateFlow()

    private val supabase = SupabaseClient.client
    private val table = "flashcards"

    /**
     * Creates a new flashcard and adds it to a specific deck.
     *
     * The provided `flashcard` object must have its `deckId` property correctly
     * set to the ID of the deck where this flashcard will be created.
     *
     * Upon successful creation, the new flashcard (including its server-assigned ID)
     * is returned and also added to an internal list of flashcards.
     *
     * @param flashcard The `Flashcard` object to be created.
     *                  Its `deckId` property **must** be populated.
     * @return A `Result` object.
     *         - On success, it contains the newly created `Flashcard` object.
     *         - On failure, it contains the `Exception` that occurred.
     */
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

    /**
     * Retrieves a specific flashcard from the database.
     *
     * @param id The ID of the flashcard to retrieve.
     * @param deckId The ID of the deck the flashcard belongs to.
     * @return The [Flashcard] object if found, otherwise null.
     *         Returns null if an error occurs during the database operation.
     */
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

    /**
     * Retrieves a list of flashcards associated with a specific deck ID from the Supabase database.
     *
     * This function performs an asynchronous operation.
     * It queries the Supabase 'table' for all flashcards where the 'deck_id' column matches the provided [deckId].
     * Upon successful retrieval, it updates the internal `_flashcards` LiveData with the fetched list and returns the list.
     * If any error occurs during the database operation, it logs the error and returns an empty list.
     *
     * @param deckId The unique identifier of the deck for which to retrieve flashcards.
     * @return A list of [Flashcard] objects belonging to the specified deck. Returns an empty list if no flashcards are found or if an error occurs.
     */
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

    /**
     * Edits an existing flashcard in the Supabase database and updates the local cache.
     *
     * This function attempts to update a flashcard record in the Supabase table specified by `table`.
     * It uses the `id` of the provided `flashcard` object to identify the record to update.
     * If the update is successful, the function decodes the updated flashcard from the response.
     * It then updates the local `_flashcards` LiveData by replacing the old flashcard with the updated one.
     *
     * @param flashcard The [Flashcard] object containing the updated data. The `id` field of this
     *                  object is crucial for identifying the flashcard to be edited.
     * @return A [Result] object:
     *         - [Result.success] containing the updated [Flashcard] object if the operation was successful.
     *         - [Result.failure] containing the [Exception] if an error occurred during the database
     *           operation or decoding. Errors are logged to Logcat.
     */
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

    /**
     * Deletes a flashcard from the database and updates the local cache.
     *
     * @param id The ID of the flashcard to delete.
     * @param deckId The ID of the deck the flashcard belongs to.
     * @return A [Result] indicating success or failure.
     *         - [Result.success] with [Unit] if the flashcard was deleted successfully.
     *         - [Result.failure] with an [Exception] if an error occurred during deletion.
     */
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

    /**
     * Counts the number of flashcards associated with a specific deck.
     *
     * This function queries the Supabase database to count the flashcards
     * where the "deck_id" column matches the provided `deckId`.
     *
     * @param deckId The unique identifier of the deck for which to count flashcards.
     * @return The number of flashcards in the specified deck. Returns 0 if an error occurs
     *         or if no flashcards are found for the given deckId.
     * @throws Exception if there's an issue with the database query (though it's caught and logged).
     */
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