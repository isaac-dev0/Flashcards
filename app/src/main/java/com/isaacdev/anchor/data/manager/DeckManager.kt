package com.isaacdev.anchor.data.manager

import com.isaacdev.anchor.data.model.Deck

interface DeckManager {
    suspend fun createDeck(deck: Deck): Result<Deck>
    suspend fun getDeck(id: String): Deck?
    suspend fun getDecks(): List<Deck>
    suspend fun editDeck(deck: Deck): Result<Deck>
    suspend fun deleteDeck(id: String): Result<Unit>
}