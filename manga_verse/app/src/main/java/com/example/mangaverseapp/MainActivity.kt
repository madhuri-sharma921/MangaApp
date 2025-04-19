package com.example.mangaverseapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController

import com.example.mangaverseapp.presentation.auth.SignInViewModel
import com.example.mangaverseapp.presentation.navigation.NavigationGraph
import com.example.mangaverseapp.presentation.navigation.Screen
import com.example.mangaverseapp.ui.theme.MangaVerseAppTheme
import com.example.mangaverseapp.utils.ModelValidator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val signInViewModel: SignInViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val isModelValid = ModelValidator.validateFaceDetectionModel(this)
        Log.d("ModelCheck", "Model is valid: $isModelValid")

        // Optional: List all assets for debugging
        val availableAssets = ModelValidator.listAvailableAssets(this)
        Log.d("AssetsList", "Available assets: $availableAssets")

        setContent {
            MangaVerseAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val currentUser by signInViewModel.currentUser.collectAsState()

                    // Determine start destination based on authentication state
                    val startDestination = if (currentUser != null) {
                        Screen.Main.route
                    } else {
                        Screen.SignIn.route
                    }

                    // Set up the navigation graph with the appropriate start destination
                    NavigationGraph(
                        navController = navController,
                        startDestination = startDestination
                    )
                }
            }
        }
    }
}