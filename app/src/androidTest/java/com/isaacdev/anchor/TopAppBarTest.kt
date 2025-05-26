package com.isaacdev.anchor

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.isaacdev.anchor.presentation.navigation.AppTopBar
import com.isaacdev.anchor.presentation.navigation.Screen
import org.junit.Rule
import org.junit.Test

class TopAppBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displayCorrectTitle() {
        composeTestRule.setContent {
            AppTopBar(
                currentScreen = Screen.DeckList,
                isLoggedIn = false,
                onSignOut = {},
                onBack = {}
            )
        }
        composeTestRule.onNodeWithText(Screen.DeckList.title).assertIsDisplayed()
    }

    @Test
    fun displayBackButton() {
        composeTestRule.setContent {
            AppTopBar(
                currentScreen = Screen.CreateDeck,
                isLoggedIn = false,
                onSignOut = {},
                onBack = {}
            )
        }
        composeTestRule.onNodeWithContentDescription("Previous Page").assertIsDisplayed()
    }

    @Test
    fun callsOnBackFunction() {
        var backClicked = false
        composeTestRule.setContent {
            AppTopBar(
                currentScreen = Screen.CreateDeck,
                isLoggedIn = false,
                onSignOut = {},
                onBack = { backClicked = true }
            )
        }
        composeTestRule.onNodeWithContentDescription("Previous Page").performClick()
        assert(backClicked)
    }

    @Test
    fun displaysSignOutButtonOnLoggedIn() {
        composeTestRule.setContent {
            AppTopBar(
                currentScreen = Screen.Home,
                isLoggedIn = true,
                onSignOut = {},
                onBack = {}
            )
        }
        composeTestRule.onNodeWithContentDescription("Sign Out").assertIsDisplayed()
    }

    @Test
    fun callsSignOutFunction() {
        var signOutClicked = false
        composeTestRule.setContent {
            AppTopBar(
                currentScreen = Screen.Home,
                isLoggedIn = true,
                onSignOut = { signOutClicked = true },
                onBack = {}
            )
        }
        composeTestRule.onNodeWithContentDescription("Sign Out").performClick()
        assert(signOutClicked)
    }
}