package com.isaacdev.anchor.presentation.fragment.decks

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.isaacdev.anchor.domain.models.Deck

/**
 * Composable function that displays a single deck item in a list.
 * It shows the deck's title and description (if available), and provides
 * buttons to edit or delete the deck. Clicking on the card itself triggers
 * the `onSelectedDeck` callback.
 *
 * A confirmation dialog is shown before deleting a deck.
 *
 * @param deck The [Deck] object to display.
 * @param onSelectedDeck Callback function invoked when the deck item is clicked.
 *                       It receives the ID of the selected deck.
 * @param onEditDeck Callback function invoked when the edit button for the deck is clicked.
 *                   It receives the ID of the deck to be edited.
 * @param onDeleteDeck Callback function invoked when the delete button for the deck is clicked
 *                     and confirmed in the dialog. It receives the ID of the deck to be deleted.
 */
@Composable
fun DeckItem(
    deck: Deck,
    onSelectedDeck: (String) -> Unit,
    onEditDeck: (String) -> Unit,
    onDeleteDeck: (String) -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelectedDeck(deck.id) },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = deck.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    deck.description?.let { description ->
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Row {
                    IconButton(onClick = { onEditDeck(deck.id) }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit deck"
                        )
                    }

                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete deck",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Deck") },
            text = { Text("Are you sure you want to delete \"${deck.title}\"? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteDeck(deck.id)
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}