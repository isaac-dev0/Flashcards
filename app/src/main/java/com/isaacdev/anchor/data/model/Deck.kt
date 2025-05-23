package com.isaacdev.anchor.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Deck(
    val id: String,
    @SerialName("user_id") val userId: String,
    var title: String,
    var description: String? = null,
    @SerialName("created_at") val createdAt: String,
)
