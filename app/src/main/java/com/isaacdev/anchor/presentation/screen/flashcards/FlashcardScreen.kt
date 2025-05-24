package com.isaacdev.anchor.presentation.screen.flashcards

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.isaacdev.anchor.presentation.fragment.flashcards.FlashcardCard
import com.isaacdev.anchor.presentation.viewmodel.flashcards.FlashcardViewModel

@Composable
fun FlashcardScreen(
    deckId: String,
    flashcardId: String,
    viewModel: FlashcardViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(flashcardId) {
        viewModel.loadFlashcard(flashcardId, deckId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            else -> {
                FlashcardCard(flashcardId)
            }
        }
    }
}