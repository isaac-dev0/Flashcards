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
     * Creates a route with query parameters.
     *
     * This function takes a route string and a variable number of query parameter key-value pairs.
     * It constructs a new route string by:
     * 1. Extracting the base route (before any existing query parameters).
     * 2. Processing existing query parameters in the original route. If a query parameter
     *    from the input `queryParams` matches an existing one by name, the value from
     *    `queryParams` is used. Otherwise, the existing parameter is kept as a placeholder
     *    (e.g., "{paramName}").
     * 3. Appending any new query parameters from `queryParams` that were not already present.
     * 4. Joining all query parameters with "&" and appending them to the base route,
     *    prefixed by "?". If there are no query parameters, only the base route is returned.
     *
     * @param queryParams A variable number of `Pair<String, String>` objects, where the
     *                    first element is the query parameter name and the second is its value.
     * @return A new string representing the route with the combined and updated query parameters.
     *         Returns the base route if no query parameters are present. */
    fun createRouteWithQueryParam(vararg queryParams: Pair<String, String>): String {
        val routeWithoutParams = route.substringBefore("?")
        val existingParams = route.substringAfter("?", "")

        val paramsList = mutableListOf<String>()

        if (existingParams.isNotEmpty()) {
            val paramNames = existingParams.split("&").map { it.substringBefore("=") }
            paramNames.forEach { paramName ->
                val paramValue = queryParams.find { it.first == paramName }?.second ?: "{$paramName}"
                paramsList.add("$paramName=$paramValue")
            }
        }

        queryParams.forEach { (key, value) ->
            if (!paramsList.any { it.startsWith("$key=") }) {
                paramsList.add("$key=$value")
            }
        }

        return if (paramsList.isEmpty()) routeWithoutParams else "$routeWithoutParams?${paramsList.joinToString("&")}"
    }

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