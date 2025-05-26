package com.isaacdev.anchor

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.isaacdev.anchor.presentation.screen.HomeScreen
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeScreenDisplaysWelcomeMessageAndInstructions() {
        composeTestRule.setContent {
            HomeScreen(onNavigateToDeckList = {})
        }
        composeTestRule.onNodeWithText("Welcome to Anchor!").assertIsDisplayed()
        composeTestRule.onNodeWithText("This is a simple Flashcard learning app, built with Jetpack Compose. You can create decks, which contain flashcards. Flashcards can be reviewed and edited using the Review button which operates on a timed learning system. Click the card to flip and view the answer.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Get Started").assertIsDisplayed()
    }

    @Test
    fun clickingGetStartedTriggersNavigation() {
        var navigateToDeckListCalled = false
        composeTestRule.setContent {
            HomeScreen(onNavigateToDeckList = { navigateToDeckListCalled = true })
        }
        composeTestRule.onNodeWithText("Get Started").performClick()
        assert(navigateToDeckListCalled)
    }
}