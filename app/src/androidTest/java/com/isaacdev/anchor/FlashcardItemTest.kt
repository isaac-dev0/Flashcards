package com.isaacdev.anchor

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.isaacdev.anchor.domain.models.Flashcard
import com.isaacdev.anchor.domain.models.enums.Difficulty
import com.isaacdev.anchor.presentation.fragment.flashcards.FlashcardItem
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime

class FlashcardItemTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testFlashcard = Flashcard(
        id = "testFlashcardId",
        deckId = "testDeckId",
        question = "What is the capital of France?",
        answer = "Paris",
        difficulty = Difficulty.MEDIUM,
        createdAt = LocalDateTime.now().toString()
    )

    @Test
    fun flashcardItemDisplaysQuestionAndDifficulty() {
        composeTestRule.setContent {
            FlashcardItem(
                flashcard = testFlashcard,
                onSelectedFlashcard = {},
                onEditFlashcard = {},
                onDeleteFlashcard = {}
            )
        }
        composeTestRule.onNodeWithText(testFlashcard.question).assertIsDisplayed()
        composeTestRule.onNodeWithText("Difficulty: ${testFlashcard.difficulty}").assertIsDisplayed()
    }

    @Test
    fun flashcardItemClickCallsOnSelectedFlashcard() {
        var selectedId = ""
        composeTestRule.setContent {
            FlashcardItem(
                flashcard = testFlashcard,
                onSelectedFlashcard = { id -> selectedId = id },
                onEditFlashcard = {},
                onDeleteFlashcard = {}
            )
        }
        composeTestRule.onNodeWithText(testFlashcard.question).performClick()
        assert(selectedId == testFlashcard.id)
    }

    @Test
    fun clickEditIconCallsOnEditFlashcard() {
        var editId = ""
        composeTestRule.setContent {
            FlashcardItem(
                flashcard = testFlashcard,
                onSelectedFlashcard = {},
                onEditFlashcard = { id -> editId = id },
                onDeleteFlashcard = {}
            )
        }
        composeTestRule.onNodeWithContentDescription("Edit flashcard").performClick()
        assert(editId == testFlashcard.id)
    }

    @Test
    fun clickDeleteIconShowsDeleteDialog() {
        composeTestRule.setContent {
            FlashcardItem(
                flashcard = testFlashcard,
                onSelectedFlashcard = {},
                onEditFlashcard = {},
                onDeleteFlashcard = {}
            )
        }
        composeTestRule.onNodeWithContentDescription("Delete flashcard").performClick()
        composeTestRule.onNodeWithText("Delete Flashcard").assertIsDisplayed()
        composeTestRule.onNodeWithText("Are you sure you want to delete \"${testFlashcard.question}\"? This action cannot be undone.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Delete").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancel").assertIsDisplayed()
    }

    @Test
    fun clickingDeleteInDialogCallsOnDeleteFlashcard() {
        var deletedId = ""
        composeTestRule.setContent {
            FlashcardItem(
                flashcard = testFlashcard,
                onSelectedFlashcard = {},
                onEditFlashcard = {},
                onDeleteFlashcard = { id -> deletedId = id }
            )
        }
        composeTestRule.onNodeWithContentDescription("Delete flashcard").performClick()
        composeTestRule.onNodeWithText("Delete").performClick()
        assert(deletedId == testFlashcard.id)
    }

    @Test
    fun clickingCancelInDialogDismissesDialog() {
        composeTestRule.setContent {
            FlashcardItem(
                flashcard = testFlashcard,
                onSelectedFlashcard = {},
                onEditFlashcard = {},
                onDeleteFlashcard = {}
            )
        }
        composeTestRule.onNodeWithContentDescription("Delete flashcard").performClick()
        composeTestRule.onNodeWithText("Cancel").performClick()
        composeTestRule.onNodeWithText("Delete Flashcard").assertDoesNotExist()
    }
}