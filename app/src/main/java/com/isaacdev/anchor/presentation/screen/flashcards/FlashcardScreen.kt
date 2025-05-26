package com.isaacdev.anchor.presentation.screen.flashcards

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.isaacdev.anchor.presentation.fragment.flashcards.FlashcardCard
import com.isaacdev.anchor.presentation.viewmodel.flashcards.FlashcardViewModel

/**
 * Composable function that displays a single flashcard.
 *
 * This screen is responsible for loading and displaying a flashcard based on the provided
 * `deckId` and `flashcardId`. It utilizes a [FlashcardViewModel] to manage the state
 * and fetch flashcard data.
 *
 * The UI displays a loading indicator while the flashcard is being fetched. Once loaded,
 * the [FlashcardCard] composable is used to display the flashcard content. If an error
 * occurs during loading, an error message is shown. If no data is available (e.g., initial state),
 * an "Idle/No Data" message is displayed.
 *
 * @param deckId The ID of the deck to which the flashcard belongs.
 * @param flashcardId The ID of the flashcard to display.
 * @param viewModel The [FlashcardViewModel] instance used to manage the UI state and data fetching.
 *                  Defaults to an instance provided by Hilt.
 */
@Composable
fun FlashcardScreen(
    deckId: String,
    flashcardId: String,
    viewModel: FlashcardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Log.d("FlashcardScreen", "Composed with deckId: $deckId, flashcardId: $flashcardId")

    LaunchedEffect(flashcardId, deckId) {
        viewModel.loadFlashcard(flashcardId, deckId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            uiState.flashcard != null -> {
                FlashcardCard(
                    uiState.flashcard,
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }

            uiState.errorMessage?.isNotEmpty() == true -> {
                Text("Error: ${uiState.errorMessage}", modifier = Modifier.align(Alignment.Center))
            }

            else -> {
                Text("Idle/No Data", modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}