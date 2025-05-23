package com.isaacdev.anchor.presentation.screen.decks

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
import com.isaacdev.anchor.presentation.viewmodel.decks.DeckCreateViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.isaacdev.anchor.data.model.Deck
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID

@Composable
fun DeckCreateScreen(
    deckId: String? = null,
    onDeckCreated: () -> Unit,
    viewModel: DeckCreateViewModel = viewModel()
) {

    val isEditMode = deckId != null
    val scope = rememberCoroutineScope()

    val user by viewModel.user.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val deck by viewModel.deck.collectAsState()

    var deckTitle by remember { mutableStateOf("") }
    var deckDescription by remember { mutableStateOf("") }

    LaunchedEffect(deckId) {
        if (isEditMode) {
            viewModel.loadDeck(deckId!!)
        }
    }

    LaunchedEffect(deck) {
        deck?.let {
            deckTitle = it.title
            deckDescription = it.description!!
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        OutlinedTextField(
            value = deckTitle,
            onValueChange = { deckTitle = it },
            label = { Text("Deck Name") },
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )

        OutlinedTextField(
            value = deckDescription,
            onValueChange = { deckDescription = it },
            label = { Text("Description") },
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth(),
            singleLine = false,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
        )

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
                    val userId = user?.id  ?: return@launch
                    val deckData = if (isEditMode) {
                        deck!!.copy(
                            title = deckTitle,
                            description = deckDescription
                        )
                    } else {
                        Deck(
                            id = UUID.randomUUID().toString(),
                            title = deckTitle,
                            description = deckDescription,
                            userId = userId,
                            createdAt = LocalDateTime.now().toString()
                        )
                    }

                    val result = if (isEditMode) {
                        viewModel.editDeck(deckData)
                    } else {
                        viewModel.createDeck(deckData)
                    }

                    if (result.isSuccess) {
                        onDeckCreated()
                    }
                }
            },
            enabled = !isLoading && listOf(deckTitle, deckDescription).all { it.isNotBlank() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(if (isEditMode) "Edit Deck" else "Create Deck")
        }

        if (isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}