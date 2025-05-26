package com.isaacdev.anchor.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Composable function that displays the home screen of the Anchor app.
 *
 * This screen welcomes the user and provides a brief description of the app's functionality.
 * It includes a "Get Started" button that navigates the user to the deck list screen.
 *
 * @param onNavigateToDeckList A lambda function that is invoked when the "Get Started" button is clicked.
 *                             This function should handle the navigation to the deck list screen.
 */
@Composable
fun HomeScreen(onNavigateToDeckList: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome to Anchor!",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "This is a simple Flashcard learning app, built with Jetpack Compose. You can create decks, which contain flashcards. Flashcards can be reviewed and edited using the Review button which operates on a timed learning system. Click the card to flip and view the answer.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onNavigateToDeckList
        ) {
            Text("Get Started")
        }
    }
}