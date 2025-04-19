package com.example.mangaverseapp.presentation.navigation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.getValue
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.mangaverseapp.domain.model.MangaDetailState
import com.example.mangaverseapp.presentation.auth.SignInScreen
import com.example.mangaverseapp.presentation.auth.SignInViewModel
import com.example.mangaverseapp.presentation.auth.SignUpScreen
import com.example.mangaverseapp.presentation.auth.SignUpViewModel
import com.example.mangaverseapp.presentation.main.MainScreen
import com.example.mangaverseapp.presentation.facerecognition.PermissionRequestLauncher
import com.example.mangaverseapp.presentation.facerecognition.ScreenFaceDetector
import com.example.mangaverseapp.ui.MangaDetailContent
import com.example.mangaverseapp.ui.MangaDetailViewModel
import com.example.mangaverseapp.ui.MangaListScreen
import com.example.mangaverseapp.ui.MangaListViewModel

sealed class Screen(val route: String) {
    object SignIn : Screen("sign_in")
    object SignUp : Screen("sign_up")
    object Main : Screen("main")
    object MangaList : Screen("manga_list")
    object MangaDetails : Screen("manga_details/{mangaId}") {
        fun createRoute(mangaId: String) = "manga_details/$mangaId"
    }
    object FaceRecognition : Screen("face_recognition")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationGraph(
    navController: NavHostController,
    startDestination: String = Screen.SignIn.route,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Sign In Screen
        composable(Screen.SignIn.route) {
            val viewModel: SignInViewModel = hiltViewModel()
            SignInScreen(
                viewModel = viewModel,
                onNavigateToSignUp = {
                    navController.navigate(Screen.SignUp.route)
                },
                onSignInSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.SignIn.route) { inclusive = true }
                    }
                }
            )
        }

        // Sign Up Screen
        composable(Screen.SignUp.route) {
            val viewModel: SignUpViewModel = hiltViewModel()
            SignUpScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.navigateUp()
                },
                onSignUpSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.SignIn.route) { inclusive = true }
                    }
                }
            )
        }

        // Manga List Screen
        composable(Screen.MangaList.route) {
            val viewModel: MangaListViewModel = hiltViewModel()
            MangaListScreen(
                viewModel = viewModel,
                navigateToDetail = { mangaId ->
                    navController.navigate(Screen.MangaDetails.createRoute(mangaId))
                }
            )
        }

        // Main Screen - contains its own nested navigation with bottom nav
        composable(Screen.Main.route) {
            MainScreen(
                onNavigateToMangaList = {
                    navController.navigate(Screen.MangaList.route)
                },
                onNavigateToMangaDetails = { mangaId ->
                    navController.navigate(Screen.MangaDetails.createRoute(mangaId))
                }
            )
        }

        // Manga Details Screen
        composable(
            route = Screen.MangaDetails.route,
            arguments = listOf(navArgument("mangaId") { type = NavType.StringType })
        ) { backStackEntry ->
            val mangaId = backStackEntry.arguments?.getString("mangaId") ?: ""
            val viewModel: MangaDetailViewModel = hiltViewModel()

            // Trigger loading of manga details
            LaunchedEffect(mangaId) {
                viewModel.loadMangaDetails(mangaId)
            }

            // Observe the state
            val mangaDetailState by viewModel.mangaDetailState.collectAsStateWithLifecycle()

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = when (val state = mangaDetailState) {
                                    is MangaDetailState.Success -> state.manga.title
                                    else -> "Manga Details"
                                }
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                            }
                        }
                    )
                }
            ) { paddingValues ->
                when (val state = mangaDetailState) {
                    MangaDetailState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    is MangaDetailState.Success -> {
                        MangaDetailContent(
                            manga = state.manga,
                            modifier = Modifier.padding(paddingValues)
                        )
                    }
                    is MangaDetailState.Error -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = state.message,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }

        // Face Recognition Screen
        composable(Screen.FaceRecognition.route) {
            val context = LocalContext.current
            val coroutineScope = rememberCoroutineScope()

            val permissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (!isGranted) {
                    // Handle permission denied scenario
                    navController.navigateUp()
                }
            }

            // Create permission request launcher
            val permissionRequestLauncher: (callback: (Boolean) -> Unit) -> PermissionRequestLauncher = { callback ->
                object : PermissionRequestLauncher {
                    override fun launch(permission: String) {
                        permissionLauncher.launch(permission)
                    }
                }
            }

            // Face detector screen
            ScreenFaceDetector(
                context = context,
                coroutineScope = coroutineScope,
                cameraPermissionRequest = permissionRequestLauncher
            )
        }
    }
}