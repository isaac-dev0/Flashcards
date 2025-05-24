package com.isaacdev.anchor.presentation.fragment.flashcards

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.isaacdev.anchor.domain.models.Flashcard

@Composable
fun FlashcardItem(
    flashcard: Flashcard,
    onSelectedFlashcard: (String) -> Unit,
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onSelectedFlashcard(flashcard.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = flashcard.question,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = flashcard.difficulty.toString(),
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}