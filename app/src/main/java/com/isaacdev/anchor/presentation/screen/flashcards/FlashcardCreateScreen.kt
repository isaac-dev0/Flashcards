package com.isaacdev.anchor.presentation.screen.flashcards

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.isaacdev.anchor.presentation.viewmodel.flashcards.FlashcardCreateViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.isaacdev.anchor.domain.models.Flashcard
import com.isaacdev.anchor.domain.models.enums.Difficulty
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardCreateScreen(
    flashcardId: String? = null,
    deckId: String,
    onFlashcardCreated: () -> Unit,
    viewModel: FlashcardCreateViewModel = viewModel()
) {

    val isEditMode = flashcardId != null
    val scope = rememberCoroutineScope()

    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val flashcard by viewModel.flashcard.collectAsState()

    var flashcardQuestion by remember { mutableStateOf("") }
    var flashcardAnswer by remember { mutableStateOf("") }
    var difficulty by remember { mutableStateOf(Difficulty.EASY) }

    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(flashcardId) {
        if (isEditMode) {
            viewModel.loadFlashcard(flashcardId!!, deckId)
        }
    }

    LaunchedEffect(flashcard) {
        flashcard?.let {
            flashcardQuestion = it.question
            flashcardAnswer = it.answer
            difficulty = it.difficulty
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        OutlinedTextField(
            value = flashcardQuestion,
            onValueChange = { flashcardQuestion = it },
            label = { Text("Question") },
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )

        OutlinedTextField(
            value = flashcardAnswer,
            onValueChange = { flashcardAnswer = it },
            label = { Text("Answer") },
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth(),
            singleLine = false,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier
                .padding(16.dp)
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

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Button(
            onClick = {
                scope.launch {
                    val flashcardData = if (isEditMode) {
                        flashcard!!.copy(
                            question = flashcardQuestion,
                            answer = flashcardAnswer,
                            difficulty = difficulty
                        )
                    } else {
                        Flashcard(
                            id = UUID.randomUUID().toString(),
                            deckId = deckId,
                            question = flashcardQuestion,
                            answer = flashcardAnswer,
                            difficulty = difficulty,
                            createdAt = LocalDateTime.now().toString()
                        )
                    }

                    val result = if (isEditMode) {
                        viewModel.editFlashcard(flashcardData)
                    } else {
                        viewModel.createFlashcard(flashcardData)
                    }

                    if (result.isSuccess) {
                        onFlashcardCreated()
                    }
                }
            },
            enabled = !isLoading && listOf(flashcardQuestion, flashcardAnswer).all { it.isNotBlank() } && difficulty != null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(if (isEditMode) "Edit Flashcard" else "Create Flashcard")
        }

        if (isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}