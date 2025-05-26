package com.isaacdev.anchor.presentation.screen.decks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.isaacdev.anchor.presentation.fragment.decks.DeckItem
import com.isaacdev.anchor.presentation.fragment.decks.EmptyDeckList
import com.isaacdev.anchor.presentation.viewmodel.decks.DeckListViewModel

@Composable
fun DeckListScreen(
    onCreateDeck: () -> Unit,
    onSelectedDeck: (String) -> Unit,
    onEditDeck: (String) -> Unit,
    viewModel: DeckListViewModel = hiltViewModel()
) {

    val decks by viewModel.decks.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadDecks()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading && decks.isEmpty() -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            decks.isEmpty() -> {
                EmptyDeckList(
                    onCreateDeck = onCreateDeck,
                    onRetry = viewModel::loadDecks,
                    hasError = uiState.errorMessage != null,
                    errorMessage = uiState.errorMessage
                )
            }

            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items = decks, key = { it.id }) {
                        DeckItem(
                            deck = it,
                            onSelectedDeck = onSelectedDeck,
                            onEditDeck = onEditDeck,
                            onDeleteDeck = viewModel::deleteDeck
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = onCreateDeck,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Create Deck"
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