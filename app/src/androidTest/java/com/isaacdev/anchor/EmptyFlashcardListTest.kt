package com.isaacdev.anchor

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.isaacdev.anchor.presentation.fragment.flashcards.EmptyFlashcardList
import org.junit.Rule
import org.junit.Test

class EmptyFlashcardListTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun hasNoError_displaysNoFlashcardsMessageAndCreateButton() {
        composeTestRule.setContent {
            EmptyFlashcardList(
                onCreateFlashcard = {},
                onRetry = {},
                hasError = false,
                errorMessage = null
            )
        }
        composeTestRule.onNodeWithText("No flashcards yet").assertIsDisplayed()
        composeTestRule.onNodeWithText("Create your first flashcard to get started!").assertIsDisplayed()
        composeTestRule.onNodeWithText("Create Flashcard").assertIsDisplayed()
    }

    @Test
    fun hasError_displaysErrorMessageAndRetryButton() {
        val errorMessage = "Failed to fetch flashcards."
        composeTestRule.setContent {
            EmptyFlashcardList(
                onCreateFlashcard = {},
                onRetry = {},
                hasError = true,
                errorMessage = errorMessage
            )
        }
        composeTestRule.onNodeWithText("Failed to load flashcards").assertIsDisplayed()
        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
        composeTestRule.onNodeWithText("Try Again").assertIsDisplayed()
        composeTestRule.onNodeWithText("Create Flashcard").assertIsDisplayed()
    }

    @Test
    fun hasNoError_displaysDefaultErrorMessageAndRetryButton() {
        composeTestRule.setContent {
            EmptyFlashcardList(
                onCreateFlashcard = {},
                onRetry = {},
                hasError = true,
                errorMessage = null
            )
        }
        composeTestRule.onNodeWithText("Failed to load flashcards").assertIsDisplayed()
        composeTestRule.onNodeWithText("Something went wrong").assertIsDisplayed()
        composeTestRule.onNodeWithText("Try Again").assertIsDisplayed()
    }

    @Test
    fun clickCreateFlashcard() {
        var createClicked = false
        composeTestRule.setContent {
            EmptyFlashcardList(
                onCreateFlashcard = { createClicked = true },
                onRetry = {},
                hasError = false,
                errorMessage = null
            )
        }
        composeTestRule.onNodeWithText("Create Flashcard").performClick()
        assert(createClicked)
    }

    @Test
    fun hasError_clickRetryButton() {
        var retryClicked = false
        composeTestRule.setContent {
            EmptyFlashcardList(
                onCreateFlashcard = {},
                onRetry = { retryClicked = true },
                hasError = true,
                errorMessage = "Some error"
            )
        }
        composeTestRule.onNodeWithText("Try Again").performClick()
        assert(retryClicked)
    }
}