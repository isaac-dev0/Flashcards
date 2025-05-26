package com.isaacdev.anchor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.isaacdev.anchor.presentation.navigation.ScreenController
import com.isaacdev.anchor.presentation.theme.AnchorTheme
import com.isaacdev.anchor.presentation.viewmodel.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AnchorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AnchorApp()
                }
            }
        }
    }
}

@Composable
fun AnchorApp(authViewModel: AuthViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val authState by authViewModel.authState.collectAsState()

    ScreenController(
        navController = navController,
        authState = authState,
        onSignOut = {
            authViewModel.signOut()
        }
    )
}