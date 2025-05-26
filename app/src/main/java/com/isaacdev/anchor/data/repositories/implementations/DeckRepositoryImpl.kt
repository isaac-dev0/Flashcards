package com.isaacdev.anchor.data.repositories.implementations

import android.util.Log
import com.isaacdev.anchor.data.modules.IoDispatcher
import com.isaacdev.anchor.domain.exceptions.DeckException
import com.isaacdev.anchor.data.repositories.DeckRepository
import com.isaacdev.anchor.domain.models.Deck
import com.isaacdev.anchor.domain.validators.DeckValidator
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [DeckRepository] that interacts with a Supabase backend for deck data.
 *
 * This class is responsible for CRUD (Create, Read, Update, Delete) operations on decks.
 * It uses a [io.github.jan.supabase.SupabaseClient] for database interactions,
 * a [DeckValidator] for validating deck data before persistence, and an [IoDispatcher]
 * to ensure database operations are performed on a background thread.
 *
 * It also maintains an in-memory [StateFlow] of decks (`_decks`) which is updated
 * after successful database operations to provide a reactive stream of the current
 * list of decks.
 *
 * @property supabaseClient The Supabase client used for database communication.
 * @property deckValidator The validator used to ensure deck data integrity before saving.
 * @property ioDispatcher The coroutine dispatcher for running I/O bound operations.
 */
@Singleton
class DeckRepositoryImpl @Inject constructor(
    private val supabaseClient: io.github.jan.supabase.SupabaseClient,
    private val deckValidator: DeckValidator,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
): DeckRepository {

    private val _decks = MutableStateFlow<List<Deck>>(emptyList())
    override val decks: StateFlow<List<Deck>> = _decks.asStateFlow()

    private val table = "decks"

    private val database = supabaseClient

    /**
     * Creates a new deck in the database.
     *
     * This function first validates the provided [deck] using [deckValidator].
     * If validation fails, it returns a [Result.failure] with the validation error.
     * Otherwise, it inserts the deck into the database.
     * On successful insertion, the newly created deck is added to the local [_decks] StateFlow
     * and a [Result.success] containing the created deck is returned.
     *
     * If any exception occurs during the process (e.g., database error),
     * an error is logged, and a [Result.failure] with a [DeckException.CreationFailed] is returned.
     *
     * @param deck The [Deck] object to be created.
     * @return A [Result] instance. If successful, it contains the created [Deck].
     *         If an error occurs, it contains a [DeckException].
     */
    override suspend fun createDeck(deck: Deck): Result<Deck> = withContext(ioDispatcher) {
        try {
            deckValidator.validateDeck(deck).getOrElse { error ->
                return@withContext Result.failure(error)
            }
            val response = database.from(table)
                .insert(deck) { select() }
                .decodeSingle<Deck>()
            _decks.update { currentDecks -> currentDecks + response }
            Result.success(response)
        } catch (e: Exception) {
            Log.e("DeckRepository", "Error creating deck", e)
            Result.failure(DeckException.CreationFailed(e.message ?: "Unknown error"))
        }
    }

    /**
     * Retrieves a deck by its ID.
     *
     * This function fetches a deck from the database based on the provided ID.
     * It operates on the IO dispatcher to avoid blocking the main thread.
     *
     * @param id The ID of the deck to retrieve. Must not be blank.
     * @return A [Result] containing the [Deck] if found, or a [DeckException] if an error occurs.
     *         Possible exceptions include [DeckException.InvalidId] if the ID is blank,
     *         or [DeckException.NotFound] if the deck is not found or another error occurs during fetching.
     */
    override suspend fun getDeck(id: String): Result<Deck> = withContext(ioDispatcher) {
        try {
            if (id.isBlank()) {
                return@withContext Result.failure(DeckException.InvalidId("Deck ID cannot be empty."))
            }
            val deck = database.from(table)
                .select { filter { eq("id", id) } }
                .decodeSingle<Deck>()
            Result.success(deck)
        } catch (e: Exception) {
            Log.e("DeckRepository", "Error getting deck with ID: $id", e)
            Result.failure(DeckException.NotFound("Deck not found"))
        }
    }

    /**
     * Retrieves all decks from the database.
     *
     * This function fetches all decks from the "decks" table in the Supabase database.
     * On successful retrieval, it updates the local `_decks` StateFlow with the new list
     * and returns a [Result.success] containing the list of [Deck] objects.
     * If an error occurs during the database operation, it logs the error and
     * returns a [Result.failure] with a [DeckException.FetchFailed].
     *
     * @return A [Result] object which is either a [Result.success] containing a list of [Deck] objects
     *         or a [Result.failure] containing a [DeckException.FetchFailed].
     */
    override suspend fun getDecks(): Result<List<Deck>> = withContext(ioDispatcher) {
        try {
            val response = database.from(table)
                .select()
                .decodeList<Deck>()
            _decks.value = response
            Result.success(response)
        } catch (e: Exception) {
            Log.e("DeckRepository", "Error getting decks", e)
            Result.failure(DeckException.FetchFailed(e.message ?: "Unknown error"))
        }
    }

    /**
     * Edits an existing deck in the database.
     *
     * This function first validates the provided deck. If validation fails, it returns a [Result.failure]
     * with the corresponding [DeckException].
     * If validation is successful, it attempts to update the deck in the Supabase database.
     * If the update is successful, it updates the local in-memory list of decks and returns a [Result.success]
     * with the updated deck.
     * If any exception occurs during the database operation, it logs the error and returns a [Result.failure]
     * with a [DeckException.UpdateFailed].
     *
     * @param deck The [Deck] object to be updated. The `id` field of this deck must match an existing deck.
     * @return A [Result] object that encapsulates either the updated [Deck] on success or a [DeckException] on failure.
     */
    override suspend fun editDeck(deck: Deck): Result<Deck> = withContext(ioDispatcher) {
        try {
            deckValidator.validateDeck(deck).getOrElse { error ->
                return@withContext Result.failure(error)
            }
            val response = database.from(table)
                .update(deck) {
                    filter { eq("id", deck.id) }
                }
                .decodeSingle<Deck>()
            _decks.update { currentDecks ->
                currentDecks.map { if (it.id == deck.id) response else it }
            }
            Result.success(response)
        } catch (e: Exception) {
            Log.e("DeckRepository", "Error editing deck", e)
            Result.failure(DeckException.UpdateFailed(e.message ?: "Unknown error"))
        }
    }

    /**
     * Deletes a deck with the specified ID.
     *
     * This function attempts to delete a deck from both the remote database and the local cache.
     * It first validates that the provided ID is not blank. If it is, a [DeckException.InvalidId]
     * is returned.
     *
     * It then proceeds to delete the deck from the Supabase database.
     * After successful deletion from the database, the local `_decks` StateFlow is updated
     * to remove the deleted deck.
     *
     * @param id The unique identifier of the deck to be deleted.
     * @return A [Result] indicating the outcome of the operation.
     *         - [Result.success] with [Unit] if the deck was successfully deleted.
     *         - [Result.failure] with a [DeckException] if an error occurred.
     *           - [DeckException.InvalidId] if the provided ID is blank.
     *           - [DeckException.DeletionFailed] if any other error occurs during the deletion process.
     */
    override suspend fun deleteDeck(id: String): Result<Unit> = withContext(ioDispatcher) {
        try {
            if (id.isBlank()) {
                return@withContext Result.failure(DeckException.InvalidId("Deck ID cannot be empty."))
            }

            database.from(table)
                .delete { filter { eq("id", id) } }

            _decks.update { currentDecks -> currentDecks.filter { it.id != id } }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("DeckRepository", "Error deleting deck", e)
            Result.failure(DeckException.DeletionFailed(e.message ?: "Unknown error"))
        }
    }
}