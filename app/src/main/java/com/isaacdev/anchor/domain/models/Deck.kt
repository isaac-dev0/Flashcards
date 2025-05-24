package com.isaacdev.anchor.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a deck of flashcards.
 *
 * @property id The unique identifier of the deck.
 * @property userId The unique identifier of the user who owns the deck.
 * @property title The title of the deck.
 * @property description An optional description of the deck.
 * @property createdAt The timestamp indicating when the deck was created.
 */
@Serializable
data class Deck(
    val id: String,
    @SerialName("user_id") val userId: String,
    var title: String,
    var description: String? = null,
    @SerialName("created_at") val createdAt: String,
)
