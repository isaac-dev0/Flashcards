package com.isaacdev.anchor.presentation.fragment.flashcards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * A Composable function that displays a message indicating that the flashcard list is empty or an error occurred.
 *
 * This function provides a user-friendly way to inform the user when there are no flashcards to display
 * or when an error prevented the flashcards from loading. It includes options to retry loading or
 * create a new flashcard.
 *
 * @param onCreateFlashcard A lambda function to be invoked when the user clicks the "Create Flashcard" button.
 * @param onRetry A lambda function to be invoked when the user clicks the "Try Again" button (only visible if `hasError` is true).
 * @param hasError A boolean indicating whether an error occurred while loading the flashcards. If true, an error message and a "Try Again" button will be shown.
 * @param errorMessage An optional string containing a specific error message to display if `hasError` is true. If null, a generic error message will be used.
 */
@Composable
fun EmptyFlashcardList(
    onCreateFlashcard: () -> Unit,
    onRetry: () -> Unit,
    hasError: Boolean,
    errorMessage: String?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (hasError) "Failed to load flashcards" else "No flashcards yet",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Text(
            text = if (hasError) {
                errorMessage ?: "Something went wrong"
            } else {
                "Create your first flashcard to get started!"
            },
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (hasError) {
            Button(onClick = onRetry) {
                Text("Try Again")
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(onClick = onCreateFlashcard) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text("Create Flashcard")
        }
    }
}