package com.isaacdev.anchor.data.repository

import android.util.Log
import com.isaacdev.anchor.data.SupabaseClient
import com.isaacdev.anchor.data.manager.DeckManager
import com.isaacdev.anchor.data.model.Deck
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DeckRepository: DeckManager {

    private val _decks = MutableStateFlow<List<Deck>>(emptyList())
    val decks: StateFlow<List<Deck>> = _decks.asStateFlow()

    private val supabase = SupabaseClient.client
    private val table = "decks"

    /**
     * Creates a new deck in the database.
     *
     * @param deck The deck object to be created.
     * @return A Result object containing the created deck if successful, or an exception if an error occurred.
     */
    override suspend fun createDeck(deck: Deck): Result<Deck> {
        return try {
            val response = supabase.from(table)
                .insert(deck) { select() }
                .decodeSingle<Deck>()
            _decks.value += response
            Result.success(response)
        } catch (e: Exception) {
            Log.e("DeckRepository", "Error creating deck: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Retrieves a deck by its ID.
     *
     * @param id The ID of the deck to retrieve.
     * @return The deck with the specified ID, or null if an error occurs or the deck is not found.
     */
    override suspend fun getDeck(id: String): Deck? {
        return try {
            supabase.from(table)
                .select { filter { eq("id", id) } }
                .decodeSingle<Deck>()
        } catch (e: Exception) {
            Log.e("DeckRepository", "Error getting deck: ${e.message}")
            null
        }
    }

    /**
     * Fetches a list of decks from the Supabase database.
     *
     * This function attempts to retrieve all entries from the "decks" table (as defined by the 'table' property)
     * in the Supabase database.
     *
     * Upon successful retrieval, it decodes the response into a list of `Deck` objects.
     * This list is then used to update the internal `_decks` LiveData/StateFlow, making the latest
     * deck list available to observers.
     *
     * @return A list of `Deck` objects.
     *         Returns an empty list if an error occurs during the fetch operation.
     * @throws Exception if there's an issue with the Supabase client or network communication
     *                   (though this function catches common exceptions and logs them, returning an empty list).
     */
    override suspend fun getDecks(): List<Deck> {
        return try {
            val response = supabase.from(table)
                .select()
                .decodeList<Deck>()
            _decks.value = response
            response
        } catch (e: Exception) {
            Log.e("DeckRepository", "Error getting decks: ${e.message}")
            emptyList()
        }
    }

    /**
     * Edits an existing deck in the Supabase database and updates the local cache.
     *
     * This function attempts to update a deck in the Supabase 'table'.
     * If the update is successful, it decodes the response into a [Deck] object,
     * updates the corresponding deck in the local `_decks` LiveData, and returns
     * a [Result.success] with the updated deck.
     * If any exception occurs during the process (e.g., network error, database error),
     * it logs the error and returns a [Result.failure] with the caught exception.
     *
     * @param deck The [Deck] object containing the updated information. The `id` field of this
     *             deck is used to identify the deck to be updated in the database.
     * @return A [Result] object that is either:
     *         - [Result.success] holding the updated [Deck] if the operation was successful.
     *         - [Result.failure] holding the [Exception] if an error occurred.
     */
    override suspend fun editDeck(deck: Deck): Result<Deck> {
        return try {
            val response = supabase.from(table)
                .update(deck) {
                    filter { eq("id", deck.id) }
                }
                .decodeSingle<Deck>()
            _decks.value = _decks.value.map { if (it.id == deck.id) response else it }
            Result.success(response)
        } catch (e: Exception) {
            Log.e("DeckRepository", "Error editing deck: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Deletes a deck with the given ID from the database and updates the local cache.
     *
     * @param id The ID of the deck to delete.
     * @return A [Result] indicating success or failure.
     *         - [Result.success] with [Unit] if the deck was deleted successfully.
     *         - [Result.failure] with an [Exception] if an error occurred during deletion.
     */
    override suspend fun deleteDeck(id: String): Result<Unit> {
        return try {
            supabase.from(table)
                .delete { filter { eq("id", id) } }
            _decks.value = _decks.value.filter { it.id != id }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("DeckRepository", "Error deleting deck: ${e.message}")
            Result.failure(e)
        }
    }
}