package com.isaacdev.anchor.data.repositories.implementations

import android.util.Log
import com.isaacdev.anchor.data.database.SupabaseClient
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

@Singleton
class DeckRepositoryImpl @Inject constructor(
    private val database: SupabaseClient,
    private val deckValidator: DeckValidator,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
): DeckRepository {

    private val _decks = MutableStateFlow<List<Deck>>(emptyList())
    override val decks: StateFlow<List<Deck>> = _decks.asStateFlow()

    private val table = "decks"

    override suspend fun createDeck(deck: Deck): Result<Deck> = withContext(ioDispatcher) {
        try {
            deckValidator.validateDeck(deck).getOrElse { error ->
                return@withContext Result.failure(error)
            }
            val response = database.client.from(table)
                .insert(deck) { select() }
                .decodeSingle<Deck>()
            _decks.update { currentDecks -> currentDecks + response }
            Result.success(response)
        } catch (e: Exception) {
            Log.e("DeckRepository", "Error creating deck", e)
            Result.failure(DeckException.CreationFailed(e.message ?: "Unknown error"))
        }
    }

    override suspend fun getDeck(id: String): Result<Deck> = withContext(ioDispatcher) {
        try {
            if (id.isBlank()) {
                return@withContext Result.failure(DeckException.InvalidId("Deck ID cannot be empty."))
            }
            val deck = database.client.from(table)
                .select { filter { eq("id", id) } }
                .decodeSingle<Deck>()
            Result.success(deck)
        } catch (e: Exception) {
            Log.e("DeckRepository", "Error getting deck with ID: $id", e)
            Result.failure(DeckException.NotFound("Deck not found"))
        }
    }

    override suspend fun getDecks(): Result<List<Deck>> = withContext(ioDispatcher) {
        try {
            val response = database.client.from(table)
                .select()
                .decodeList<Deck>()
            _decks.value = response
            Result.success(response)
        } catch (e: Exception) {
            Log.e("DeckRepository", "Error getting decks", e)
            Result.failure(DeckException.FetchFailed(e.message ?: "Unknown error"))
        }
    }

    override suspend fun editDeck(deck: Deck): Result<Deck> = withContext(ioDispatcher) {
        try {
            deckValidator.validateDeck(deck).getOrElse { error ->
                return@withContext Result.failure(error)
            }
            val response = database.client.from(table)
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

    override suspend fun deleteDeck(id: String): Result<Unit> = withContext(ioDispatcher) {
        try {
            if (id.isBlank()) {
                return@withContext Result.failure(DeckException.InvalidId("Deck ID cannot be empty."))
            }

            database.client.from(table)
                .delete { filter { eq("id", id) } }

            _decks.update { currentDecks -> currentDecks.filter { it.id != id } }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("DeckRepository", "Error deleting deck", e)
            Result.failure(DeckException.DeletionFailed(e.message ?: "Unknown error"))
        }
    }
}