package com.isaacdev.anchor.presentation.fragment.flashcards

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.isaacdev.anchor.domain.models.Flashcard

/**
 * A Composable function that displays a flashcard with a flipping animation.
 *
 * This function takes a [Flashcard] object and a [Modifier] as input.
 * It displays the question side of the flashcard by default. When the card is clicked,
 * it flips over to reveal the answer side, and vice-versa.
 *
 * The flipping animation is achieved by rotating the card around its Y-axis.
 * The `cameraDistance` property is used to give the card a 3D perspective effect during the flip.
 *
 * If the provided [flashcard] is null, it displays a "No card to display." message.
 *
 * @param flashcard The [Flashcard] object to display. Can be null if no card is available.
 * @param modifier The [Modifier] to be applied to the Card.
 */
@Composable
fun FlashcardCard(
    flashcard: Flashcard?,
    modifier: Modifier
) {
    var flipped by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (flipped) 180f else 0f,
        animationSpec = tween(durationMillis = 500)
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                flipped = !flipped
            }
            .graphicsLayer {
                cameraDistance = 8 * this.density
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            flashcard?.let { card ->
                CardFace(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            rotationY = if (rotation <= 90f) 0f else 180f
                            alpha = if (rotation <= 90f) 1f else 0f
                        },
                    text = card.question
                )

                CardFace(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            rotationY = if (rotation > 90f) 0f else -180f
                            alpha = if (rotation > 90f) 1f else 0f
                        },
                    text = card.answer
                )
            } ?: run {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No card to display.")
                }
            }
        }
    }
}

/**
 * Composable function that represents a single face of a flashcard.
 * It displays text content within a Box layout, centered both horizontally and vertically.
 *
 * @param modifier Modifier for this composable. Defaults to Modifier.
 * @param text The text to be displayed on the card face.
 */
@Composable
fun CardFace(modifier: Modifier = Modifier, text: String) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(text = text)
    }
}