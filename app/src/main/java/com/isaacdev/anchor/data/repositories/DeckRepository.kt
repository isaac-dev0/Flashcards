package com.isaacdev.anchor.data.repositories

import com.isaacdev.anchor.domain.models.Deck
import kotlinx.coroutines.flow.StateFlow

interface DeckRepository {
    val decks: StateFlow<List<Deck>>
    suspend fun createDeck(deck: Deck): Result<Deck>
    suspend fun getDeck(id: String): Result<Deck>
    suspend fun getDecks(): Result<List<Deck>>
    suspend fun editDeck(deck: Deck): Result<Deck>
    suspend fun deleteDeck(id: String): Result<Unit>
}