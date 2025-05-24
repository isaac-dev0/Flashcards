package com.isaacdev.anchor.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector? = null
) {

    data object Auth: Screen("auth", "Authentication")
    data object Home: Screen("home", "Home", Icons.Default.Home)
    data object DeckList: Screen("decks", "Decks", Icons.Default.Menu)
    data object CreateDeck: Screen ("decks/create", "Create Deck")
    data object CreateFlashcard: Screen("flashcard/create/{deckId}", "Create Flashcard")
    data object EditFlashcard: Screen("flashcard/{deckId}/{id}/edit", "Edit Flashcard")
    data object FlashcardList: Screen("flashcards", "Flashcards", Icons.Default.Star)
    data object Flashcard: Screen("/flashcard/{deckId}/{id}", "Flashcard")

    /**
     * Companion object for the [Screen] sealed class.
     *
     * Provides factory methods for creating [Screen] instances from routes
     * and a list of bottom navigation items.
     */
    companion object {
        fun fromRoute(route: String): Screen {
            return when {
                route.startsWith(Auth.route) -> Auth
                route.startsWith(Home.route) -> Home
                route.startsWith(DeckList.route) -> DeckList
                route.startsWith(CreateDeck.route) -> CreateDeck
                route.startsWith(FlashcardList.route) -> FlashcardList
                route.startsWith("flashcard/create") -> CreateFlashcard
                route.matches(Regex("flashcard/\\d+/edit")) -> EditFlashcard
                route.matches(Regex("flashcard/\\d+")) -> Flashcard
                else -> Home
            }
        }

        val bottomNavItems = listOf(Home, DeckList, FlashcardList)
    }
}