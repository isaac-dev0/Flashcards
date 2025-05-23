package com.isaacdev.anchor.presentation.screen.flashcards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.isaacdev.anchor.presentation.fragment.flashcards.FlashcardItem
import com.isaacdev.anchor.presentation.viewmodel.flashcards.FlashcardListViewModel

@Composable
fun FlashcardListScreen(
    deckId: String,
    onCreateFlashcard: () -> Unit,
    onSelectedFlashcard: (String) -> Unit,
    viewModel: FlashcardListViewModel = viewModel()
) {

    val flashcards by viewModel.flashcards.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(deckId) {
        viewModel.loadFlashcards(deckId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading && flashcards.isEmpty()) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        } else if (flashcards.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No flashcards available. Let's create your first flashcard!",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )

                if (errorMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.loadFlashcards(deckId) }) {
                        Text("Retry")
                    }
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp)
            ) {
                items(flashcards) { flashcard ->
                    FlashcardItem(
                        flashcard = flashcard,
                        onSelectedFlashcard = onSelectedFlashcard
                    )
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

        if (errorMessage.isNotEmpty()) {
            Snackbar(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomCenter)
            ) {
                Text(text = errorMessage)
            }
        }
    }
}