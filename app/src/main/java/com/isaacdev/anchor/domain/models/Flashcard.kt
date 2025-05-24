package com.isaacdev.anchor.domain.models

import com.isaacdev.anchor.domain.models.enums.Difficulty
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a flashcard.
 *
 * @property id The unique identifier of the flashcard.
 * @property deckId The unique identifier of the deck this flashcard belongs to.
 * @property question The question or prompt on the flashcard.
 * @property answer The answer or response to the question.
 * @property difficulty The difficulty level of the flashcard.
 * @property createdAt The timestamp indicating when the flashcard was created.
 */
@Serializable
data class Flashcard(
    val id: String,
    @SerialName("deck_id") val deckId: String,
    val question: String,
    val answer: String,
    var difficulty: Difficulty,
    @SerialName("created_at") val createdAt: String
)
