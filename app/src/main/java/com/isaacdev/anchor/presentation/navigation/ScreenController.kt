package com.isaacdev.anchor.presentation.navigation

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.isaacdev.anchor.data.repositories.implementations.AuthState
import com.isaacdev.anchor.presentation.navigation.Screen.Companion.bottomNavItems
import com.isaacdev.anchor.presentation.screen.HomeScreen
import com.isaacdev.anchor.presentation.screen.auth.AuthScreen
import com.isaacdev.anchor.presentation.screen.decks.DeckCreateScreen
import com.isaacdev.anchor.presentation.screen.decks.DeckEditScreen
import com.isaacdev.anchor.presentation.screen.decks.DeckListScreen
import com.isaacdev.anchor.presentation.screen.flashcards.FlashcardCreateScreen
import com.isaacdev.anchor.presentation.screen.flashcards.FlashcardEditScreen
import com.isaacdev.anchor.presentation.screen.flashcards.FlashcardListScreen
import com.isaacdev.anchor.presentation.screen.flashcards.FlashcardReviewScreen
import com.isaacdev.anchor.presentation.screen.flashcards.FlashcardScreen

@Composable
fun ScreenController(
    navController: NavHostController,
    authState: AuthState,
    onSignOut: () -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = getScreenFromBackStackEntry(navBackStackEntry)

    LaunchedEffect(navController) {
        snapshotFlow { authState }.collect { newState ->
            if (newState is AuthState.LoggedIn && currentScreen == Screen.Auth) {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Auth.route) { inclusive = true }
                }
            } else if (newState is AuthState.LoggedOut) {
                navController.navigate(Screen.Auth.route) {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                currentScreen = currentScreen,
                isLoggedIn = authState is AuthState.LoggedIn,
                onSignOut = onSignOut,
                onBack = {
                    navController.popBackStack()
                }
            )
        },
        bottomBar = {
            if (authState is AuthState.LoggedIn) {
                AppBottomNavigation(
                    navController = navController,
                    currentRoute = navBackStackEntry?.destination?.route
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (authState is AuthState.LoggedIn) Screen.Home.route else Screen.Auth.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Auth.route) {
                AuthScreen(
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Auth.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToDeckList = {
                        navController.navigate(Screen.DeckList.route)
                    }
                )
            }

            composable(Screen.DeckList.route) {
                DeckListScreen(
                    onCreateDeck = {
                        navController.navigate(Screen.CreateDeck.route)
                    },
                    onSelectedDeck = { deckId ->
                        navController.navigate(
                            Screen.FlashcardList.route + "?deckId=$deckId"
                        )
                    },
                    onEditDeck = { deckId ->
                        navController.navigate(Screen.EditDeck.route + deckId)
                    }
                )
            }

            composable(Screen.CreateDeck.route) {
                DeckCreateScreen(
                    onDeckCreated = {
                        navController.popBackStack()
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = Screen.FlashcardList.route + "?deckId={deckId}",
                arguments = listOf(
                    navArgument("deckId") {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->
                val deckId = backStackEntry.arguments?.getString("deckId")
                FlashcardListScreen(
                    deckId = deckId!!,
                    onCreateFlashcard = {
                        navController.navigate(
                            Screen.CreateFlashcard.route
                                .replace("{deckId}", deckId)
                        )
                    },
                    onSelectedFlashcard = { flashcardId ->
                        navController.navigate(
                            Screen.Flashcard.route
                                .replace("{deckId}", deckId)
                                .replace("{id}", flashcardId)
                        )
                    },
                    onEditFlashcard = { flashcardId ->
                        navController.navigate(
                            Screen.EditFlashcard.route
                                .replace("{deckId}", deckId)
                                .replace("{id}", flashcardId)
                        )
                    },
                    onReview = {
                        navController.navigate(
                            Screen.FlashcardReview.route
                                .replace("{deckId}", deckId)
                        )
                    },
                )
            }

            composable(
                route = Screen.CreateFlashcard.route,
                arguments = listOf(navArgument("deckId") { type = NavType.StringType })
            ) { backStackEntry ->
                val deckId = backStackEntry.arguments?.getString("deckId") ?: return@composable
                FlashcardCreateScreen(
                    deckId = deckId,
                    onFlashcardCreated = {
                        navController.popBackStack()
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = Screen.Flashcard.route,
                arguments = listOf(
                    navArgument("deckId") { type = NavType.StringType },
                    navArgument("id") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val deckId = backStackEntry.arguments?.getString("deckId") ?: return@composable
                val flashcardId = backStackEntry.arguments?.getString("id") ?: return@composable

                FlashcardScreen(deckId = deckId, flashcardId = flashcardId)
            }

            composable(
                route = Screen.EditFlashcard.route,
                arguments = listOf(
                    navArgument("deckId") { type = NavType.StringType },
                    navArgument("id") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val deckId = backStackEntry.arguments?.getString("deckId") ?: return@composable
                val flashcardId = backStackEntry.arguments?.getString("id") ?: return@composable
                FlashcardEditScreen(
                    flashcardId = flashcardId,
                    deckId = deckId,
                    onFlashcardEdited = {
                        navController.popBackStack()
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = Screen.EditDeck.route,
                arguments = listOf(
                    navArgument("deckId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val deckId = backStackEntry.arguments?.getString("deckId") ?: return@composable
                DeckEditScreen(
                    deckId = deckId,
                    onDeckEdited = {
                        navController.popBackStack()
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = Screen.FlashcardReview.route,
                arguments = listOf(navArgument("deckId") { type = NavType.StringType })
            ) { backStackEntry ->
                val deckId = backStackEntry.arguments?.getString("deckId") ?: return@composable
                FlashcardReviewScreen(
                    deckId = deckId,
                    onReviewComplete = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    currentScreen: Screen,
    isLoggedIn: Boolean,
    onSignOut: () -> Unit,
    onBack: () -> Unit,
) {
    TopAppBar(
        title = {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { onBack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Previous Page"
                        )
                    }
                    Text(currentScreen.title)
                }
                if (isLoggedIn) {
                    Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                        IconButton(onClick = { onSignOut() }) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = "Sign Out"
                            )
                        }
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

@Composable
fun AppBottomNavigation(
    navController: NavHostController,
    currentRoute: String?
) {
    NavigationBar {
        bottomNavItems.filterNotNull().forEach { screen ->
            NavigationBarItem(
                icon = {
                    screen.icon?.let { Icon(it, contentDescription = screen.title) }
                },
                label = { Text(screen.title) },
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}

private fun getScreenFromBackStackEntry(backStackEntry: NavBackStackEntry?): Screen {
    val route = backStackEntry?.destination?.route ?: return Screen.Home
    return Screen.fromRoute(route)
}