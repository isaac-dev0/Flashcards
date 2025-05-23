package com.isaacdev.anchor.data.model

import com.isaacdev.anchor.domain.enums.Difficulty
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Flashcard(
    val id: String,
    @SerialName("deck_id") val deckId: String,
    val question: String,
    val answer: String,
    var difficulty: Difficulty,
    @SerialName("created_at") val createdAt: String
)
