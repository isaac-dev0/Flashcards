package com.isaacdev.anchor.presentation.screen.flashcards

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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import com.isaacdev.anchor.presentation.viewmodel.flashcards.FlashcardCreateViewModel
import com.isaacdev.anchor.domain.models.enums.Difficulty

/**
 * Composable function for the Flashcard Create Screen.
 *
 * This screen allows users to create a new flashcard by providing a question, answer, and difficulty level.
 * It includes input fields for the question and answer, a dropdown menu for selecting the difficulty,
 * and buttons to create the flashcard or cancel the operation.
 *
 * The screen also handles form validation, displaying error messages for invalid input,
 * and shows a loading indicator while the flashcard is being created.
 *
 * @param deckId The ID of the deck to which the new flashcard will be added.
 * @param onFlashcardCreated Callback function invoked when a new flashcard is successfully created.
 * @param onNavigateBack Callback function invoked when the user navigates back from the screen (e.g., by pressing "Cancel").
 * @param viewModel The [FlashcardCreateViewModel] used to manage the state and logic of this screen.
 *                  It is typically provided by Hilt.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardCreateScreen(
    deckId: String,
    onFlashcardCreated: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: FlashcardCreateViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()

    var flashcardQuestion by remember { mutableStateOf("") }
    var flashcardAnswer by remember { mutableStateOf("") }
    var difficulty by remember { mutableStateOf(Difficulty.EASY) }
    var questionError by remember { mutableStateOf<String?>(null) }
    var answerError by remember { mutableStateOf<String?>(null) }
    var difficultyError by remember { mutableStateOf<String?>(null) }

    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.flashcard) {
        uiState.flashcard?.let {
            flashcardQuestion = it.question
            flashcardAnswer = it.answer
            difficulty = it.difficulty
        }
    }

    fun validateForm(): Boolean {
        questionError = when {
            flashcardQuestion.isBlank() -> "Question cannot be empty"
            flashcardQuestion.length > 100 -> "Question cannot exceed 100 characters"
            else -> null
        }
        answerError = when {
            flashcardAnswer.isBlank() -> "Answer cannot be empty"
            flashcardAnswer.length > 512 -> "Answer cannot exceed 512 characters"
            else -> null
        }
        difficultyError = null

        return questionError == null && answerError == null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        OutlinedTextField(
            value = flashcardQuestion,
            onValueChange = {
                flashcardQuestion = it
                if (questionError != null) questionError = null
            },
            label = { Text("Question") },
            isError = questionError != null,
            supportingText = questionError?.let { { Text(it) } },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )

        OutlinedTextField(
            value = flashcardAnswer,
            onValueChange = {
                flashcardAnswer = it
                if (answerError != null) answerError = null
            },
            label = { Text("Answer") },
            isError = answerError != null,
            supportingText = answerError?.let { { Text(it) } },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            minLines = 3,
            maxLines = 5,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )

        Text(
            text = "${flashcardAnswer.length}/512",
            style = MaterialTheme.typography.bodySmall,
            color = if (flashcardAnswer.length > 512) MaterialTheme.colorScheme.error
            else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.End)
        )

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            OutlinedTextField(
                value = difficulty.name,
                onValueChange = {},
                readOnly = true,
                label = { Text("Difficulty") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                Difficulty.entries.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption.name) },
                        onClick = {
                            difficulty = selectionOption
                            expanded = false
                        }
                    )
                }
            }
        }

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
                        viewModel.createFlashcard(deckId, flashcardQuestion, flashcardAnswer, difficulty, onFlashcardCreated)
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
                    Text("Create Flashcard")
                }
            }
        }
    }
}