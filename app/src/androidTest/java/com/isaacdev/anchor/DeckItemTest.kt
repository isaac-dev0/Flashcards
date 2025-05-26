package com.isaacdev.anchor

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.isaacdev.anchor.domain.models.Deck
import com.isaacdev.anchor.presentation.fragment.decks.DeckItem
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime

class DeckItemTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testDeck = Deck(
        id = "8a660ec5-8444-4400-844a-742ab0f6a02e",
        title = "Test Deck",
        description = "This is a test deck",
        userId = "8a660ec5-8444-4400-844a-742ab0f6a02e",
        createdAt = LocalDateTime.now().toString()
    )

    @Test
    fun deckItemDisplaysTitleAndDescription() {
        composeTestRule.setContent {
            DeckItem(
                deck = testDeck,
                onSelectedDeck = {},
                onEditDeck = {},
                onDeleteDeck = {}
            )
        }

        composeTestRule.onNodeWithText(testDeck.title).assertIsDisplayed()
        composeTestRule.onNodeWithText(testDeck.description!!).assertIsDisplayed()
    }

    @Test
    fun deckItemClickCallsOnSelectedDeck() {
        var selectedId = ""
        composeTestRule.setContent {
            DeckItem(
                deck = testDeck,
                onSelectedDeck = { id -> selectedId = id },
                onEditDeck = {},
                onDeleteDeck = {}
            )
        }
        composeTestRule.onNodeWithText(testDeck.title).performClick()
        assert(selectedId == testDeck.id)
    }

    @Test
    fun clickEditIconCallsOnEditDeck() {
        var editId = ""
        composeTestRule.setContent {
            DeckItem(
                deck = testDeck,
                onSelectedDeck = {},
                onEditDeck = { id -> editId = id },
                onDeleteDeck = {}
            )
        }
        composeTestRule.onNodeWithContentDescription("Edit deck").performClick()
        assert(editId == testDeck.id)
    }

    @Test
    fun clickDeleteIconShowsDeleteDialog() {
        composeTestRule.setContent {
            DeckItem(
                deck = testDeck,
                onSelectedDeck = {},
                onEditDeck = {},
                onDeleteDeck = {}
            )
        }
        composeTestRule.onNodeWithContentDescription("Delete deck").performClick()
        composeTestRule.onNodeWithText("Delete Deck").assertIsDisplayed()
        composeTestRule.onNodeWithText("Are you sure you want to delete \"${testDeck.title}\"? This action cannot be undone.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Delete").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancel").assertIsDisplayed()
    }

    @Test
    fun clickingDeleteInDialogCallsOnDeleteDeck() {
        var deletedId = ""
        composeTestRule.setContent {
            DeckItem(
                deck = testDeck,
                onSelectedDeck = {},
                onEditDeck = {},
                onDeleteDeck = { id -> deletedId = id }
            )
        }
        composeTestRule.onNodeWithContentDescription("Delete deck").performClick()
        composeTestRule.onNodeWithText("Delete").performClick()
        assert(deletedId == testDeck.id)
    }

    @Test
    fun clickingCancelInDialogDismissesDialog() {
        composeTestRule.setContent {
            DeckItem(
                deck = testDeck,
                onSelectedDeck = {},
                onEditDeck = {},
                onDeleteDeck = {}
            )
        }
        composeTestRule.onNodeWithContentDescription("Delete deck").performClick()
        composeTestRule.onNodeWithText("Cancel").performClick()
        composeTestRule.onNodeWithText("Delete Deck").assertDoesNotExist()
    }
}