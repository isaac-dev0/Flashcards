package com.isaacdev.anchor.presentation.screen.flashcards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.isaacdev.anchor.presentation.fragment.flashcards.EmptyFlashcardList
import com.isaacdev.anchor.presentation.fragment.flashcards.FlashcardItem
import com.isaacdev.anchor.presentation.viewmodel.flashcards.FlashcardListViewModel

/**
 * Composable function that displays the list of flashcards for a given deck.
 *
 * This screen shows a list of flashcards, allowing users to create, select, edit, and review them.
 * It handles loading states, empty states, and error messages.
 *
 * @param deckId The ID of the deck whose flashcards are to be displayed.
 * @param onCreateFlashcard Callback invoked when the user wants to create a new flashcard.
 * @param onSelectedFlashcard Callback invoked when a flashcard is selected. It receives the ID of the selected flashcard.
 * @param onEditFlashcard Callback invoked when the user wants to edit a flashcard. It receives the ID of the flashcard to be edited.
 * @param onReview Callback invoked when the user wants to start a review session for the current deck. It receives the ID of the deck.
 * @param viewModel The [FlashcardListViewModel] used to manage the state and logic for this screen. Defaults to a Hilt-injected instance.
 */
@Composable
fun FlashcardListScreen(
    deckId: String,
    onCreateFlashcard: () -> Unit,
    onSelectedFlashcard: (String) -> Unit,
    onEditFlashcard: (String) -> Unit,
    onReview: (String) -> Unit,
    viewModel: FlashcardListViewModel = hiltViewModel()
) {

    val flashcards by viewModel.flashcards.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit, deckId) {
        viewModel.loadFlashcards(deckId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading && flashcards.isEmpty() -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            flashcards.isEmpty() -> {
                EmptyFlashcardList(
                    onCreateFlashcard = onCreateFlashcard,
                    onRetry = { viewModel.loadFlashcards(deckId) },
                    hasError = uiState.errorMessage != null,
                    errorMessage = uiState.errorMessage
                )
            }

            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items = flashcards, key = { it.id }) {
                        FlashcardItem(
                            flashcard = it,
                            onSelectedFlashcard = onSelectedFlashcard,
                            onEditFlashcard = onEditFlashcard,
                            onDeleteFlashcard = viewModel::deleteFlashcard
                        )
                    }
                }
                Button(
                    onClick = { onReview(deckId) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                ) {
                    Text("Review")
                }
            }
        }

        FloatingActionButton(
            onClick = onCreateFlashcard,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Create Flashcard"
            )
        }

        if (uiState.errorMessage != null) {
            Snackbar(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomCenter),
                action = {
                    TextButton(onClick = viewModel::clearError) {
                        Text("Dismiss")
                    }
                }
            ) {
                Text(text = uiState.errorMessage!!)
            }
        }
    }
}