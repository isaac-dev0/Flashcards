package com.isaacdev.anchor.presentation.screen.decks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.isaacdev.anchor.presentation.viewmodel.decks.DeckCreateViewModel

/**
 * Composable function for the Deck Creation screen.
 *
 * This screen allows users to input a title and an optional description to create a new deck.
 * It handles form validation, displays error messages, and communicates with the [DeckCreateViewModel]
 * to persist the new deck.
 *
 * The UI consists of:
 * - An [OutlinedTextField] for the deck title.
 * - An [OutlinedTextField] for the deck description (optional).
 * - A character counter for the description.
 * - An error message display area if any issues occur during deck creation or validation.
 * - A "Cancel" button to navigate back.
 * - A "Create Deck" button to submit the form.
 *
 * State Management:
 * - `uiState`: Observed from the [DeckCreateViewModel], containing information about the current
 *   deck being edited (if any), loading state, and error messages.
 * - `deckTitle`: Local state for the deck title input field.
 * - `deckDescription`: Local state for the deck description input field.
 * - `titleError`: Local state to display validation errors for the title.
 * - `descriptionError`: Local state to display validation errors for the description.
 *
 * Effects:
 * - A [LaunchedEffect] observes changes in `uiState.deck`. If a deck is present in the UI state
 *   (e.g., when editing an existing deck, though this screen is primarily for creation),
 *   it populates the `deckTitle` and `deckDescription` fields.
 *
 * Validation:
 * - The `validateForm` function checks:
 *     - If the `deckTitle` is blank.
 *     - If the `deckTitle` exceeds 100 characters.
 *     - If the `deckDescription` exceeds 512 characters.
 *   It updates `titleError` and `descriptionError` accordingly and returns `true` if the form is valid.
 *
 * User Actions:
 * - Changing text in the title or description fields updates the respective local state and clears
 *   any associated errors.
 * - Clicking "Cancel" invokes the `onNavigateBack` callback.
 * - Clicking "Create Deck":
 *     - First, `validateForm()` is called.
 */
@Composable
fun DeckCreateScreen(
    onDeckCreated: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: DeckCreateViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var deckTitle by remember { mutableStateOf("") }
    var deckDescription by remember { mutableStateOf("") }
    var titleError by remember { mutableStateOf<String?>(null) }
    var descriptionError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uiState.deck) {
        uiState.deck?.let { deck ->
            deckTitle = deck.title
            deckDescription = deck.description.orEmpty()
        }
    }

    fun validateForm(): Boolean {
        titleError = when {
            deckTitle.isBlank() -> "Title cannot be empty"
            deckTitle.length > 100 -> "Title cannot exceed 100 characters"
            else -> null
        }
        descriptionError = when {
            deckDescription.length > 512 -> "Description cannot exceed 512 characters"
            else -> null
        }

        return titleError == null && descriptionError == null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        OutlinedTextField(
            value = deckTitle,
            onValueChange = {
                deckTitle = it
                if (titleError != null) titleError = null
            },
            label = { Text("Deck Name") },
            isError = titleError != null,
            supportingText = titleError?.let { { Text(it) } },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )

        OutlinedTextField(
            value = deckDescription,
            onValueChange = {
                deckDescription = it
                if (descriptionError != null) descriptionError = null
            },
            label = { Text("Description (Optional)") },
            isError = descriptionError != null,
            supportingText = descriptionError?.let { { Text(it) } },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            minLines = 3,
            maxLines = 5,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
        )

        Text(
            text = "${deckDescription.length}/512",
            style = MaterialTheme.typography.bodySmall,
            color = if (deckDescription.length > 512) MaterialTheme.colorScheme.error
            else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.End)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.errorMessage != null) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = uiState.errorMessage!!,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = viewModel::clearError) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss error"
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = onNavigateBack,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    if (validateForm()) {
                        viewModel.createDeck(deckTitle, deckDescription, onDeckCreated)
                    }
                },
                enabled = !uiState.isLoading,
                modifier = Modifier.weight(1f)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Create Deck")
                }
            }
        }
    }
}