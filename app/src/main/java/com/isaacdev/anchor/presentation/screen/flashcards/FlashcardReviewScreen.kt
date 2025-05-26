package com.isaacdev.anchor.presentation.screen.flashcards

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.isaacdev.anchor.presentation.fragment.flashcards.FlashcardCard
import com.isaacdev.anchor.presentation.viewmodel.flashcards.FlashcardReviewViewModel
import kotlin.math.max

/**
 * A composable function that displays the flashcard review screen.
 *
 * This screen allows users to review flashcards from a specific deck.
 * It displays the current flashcard, a timer, and buttons to mark the card as correct or incorrect.
 * When the review is complete, it shows a completion message and a button to go back to the deck.
 *
 * @param deckId The ID of the deck to review.
 * @param onReviewComplete A callback function to be invoked when the review is complete.
 * @param viewModel The [FlashcardReviewViewModel] used to manage the state of the review.
 */
@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun FlashcardReviewScreen(
    deckId: String,
    onReviewComplete: () -> Unit,
    viewModel: FlashcardReviewViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()
    val currentCard by viewModel.currentCard.collectAsState()
    val isReviewFinished by viewModel.isReviewFinished.collectAsState()
    val remainingTime by viewModel.remainingTime.collectAsState()

    LaunchedEffect(deckId) {
        viewModel.startReview(deckId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            isReviewFinished -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Review Complete!")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onReviewComplete) {
                        Text("Back to Deck")
                    }
                }
            }

            currentCard != null -> {
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(16.dp)
                ) {
                    LinearProgressIndicator(
                        progress = { max(0f, 1f - (remainingTime.toFloat() / 30f)) },
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.tertiaryContainer
                    )
                    Text("$remainingTime seconds remaining")

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(modifier = Modifier.weight(1f), onClick = { viewModel.markCorrect() }) {
                            Text("Yes, I got it right")
                        }
                        Button(modifier = Modifier.weight(1f), onClick = { viewModel.markIncorrect() }) {
                            Text("No, I got it wrong")
                        }
                    }
                    FlashcardCard(
                        currentCard,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .fillMaxHeight(0.7f)
                    )
                }
            }
        }
    }
}