package com.isaacdev.anchor

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.isaacdev.anchor.presentation.fragment.decks.EmptyDeckList
import org.junit.Rule
import org.junit.Test

class EmptyDeckListTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun noDecksMessageDisplayed() {
        composeTestRule.setContent {
            EmptyDeckList(
                onCreateDeck = {},
                onRetry = {},
                hasError = false,
                errorMessage = null
            )
        }

        composeTestRule.onNodeWithText("No decks yet").assertIsDisplayed()
        composeTestRule.onNodeWithText("Create your first deck to get started with your flashcards!").assertIsDisplayed()
    }
}