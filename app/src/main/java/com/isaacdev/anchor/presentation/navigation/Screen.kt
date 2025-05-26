package com.isaacdev.anchor.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector? = null
) {

    data object Auth: Screen("auth", "Authentication")
    data object Home: Screen("home", "Home", Icons.Default.Home)
    data object DeckList: Screen("decks", "Decks", Icons.Default.Menu)
    data object FlashcardReview: Screen("flashcard/review/{deckId}", "Review Flashcards")
    data object CreateDeck: Screen ("decks/create", "Create Deck")
    data object EditDeck: Screen("decks/edit/{deckId}", "Edit Deck")
    data object CreateFlashcard: Screen("flashcard/create/{deckId}", "Create Flashcard")
    data object EditFlashcard: Screen("flashcard/edit/{deckId}/{id}", "Edit Flashcard")
    data object FlashcardList: Screen("flashcards", "Flashcards")
    data object Flashcard: Screen("flashcard/view/{deckId}/{id}", "Flashcard")

    companion object {
        fun fromRoute(route: String): Screen {
            return when {
                route.startsWith(Auth.route) -> Auth
                route.startsWith(Home.route) -> Home
                route.startsWith(DeckList.route) -> DeckList
                route.startsWith(CreateDeck.route) -> CreateDeck
                route.startsWith(EditDeck.route) -> EditDeck
                route.startsWith(FlashcardList.route) -> FlashcardList
                route.startsWith(CreateFlashcard.route) -> CreateFlashcard
                route.startsWith(FlashcardReview.route) -> FlashcardReview
                route.startsWith(EditFlashcard.route) -> EditFlashcard
                route.startsWith(Flashcard.route) -> Flashcard
                else -> Home
            }
        }

        val bottomNavItems: List<Screen?> = listOfNotNull(Home, DeckList)
    }
}