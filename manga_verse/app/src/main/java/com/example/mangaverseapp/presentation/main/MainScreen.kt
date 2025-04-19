package com.example.mangaverseapp.presentation.main

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mangaverseapp.presentation.facerecognition.ScreenFaceDetector
import com.example.mangaverseapp.presentation.navigation.Screen
import com.example.mangaverseapp.ui.MangaListScreen
import com.example.mangaverseapp.ui.MangaListViewModel

import android.Manifest
import com.example.mangaverseapp.presentation.facerecognition.PermissionRequestLauncher

// Sealed class for bottom navigation items
sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    object MangaList : BottomNavItem("manga_list", "Manga", Icons.Default.List)
    object FaceRecognition : BottomNavItem("face_recognition", "Face", Icons.Default.Face)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToMangaDetails: (String) -> Unit = {},
    onNavigateToMangaList: () -> Unit = {}
) {
    val navController = rememberNavController()

    val bottomNavItems = listOf(
        BottomNavItem.MangaList,
        BottomNavItem.FaceRecognition
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title
                            )
                        },
                        label = { Text(item.title) },
                        selected = currentDestination?.hierarchy?.any {
                            it.route == item.route
                        } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination
                                launchSingleTop = true
                                // Restore state when re-selecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.MangaList.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.MangaList.route) {
                val viewModel: MangaListViewModel = hiltViewModel()
                MangaListScreen(
                    viewModel = viewModel,
                    navigateToDetail = { mangaId ->
                        // Navigate to Manga Details using the passed-in navigation function
                        onNavigateToMangaDetails(mangaId)
                    }
                )
            }

            composable(BottomNavItem.FaceRecognition.route) {
                val context = LocalContext.current
                val coroutineScope = rememberCoroutineScope()

                // Permission launcher for camera
                val cameraPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                    onResult = { isGranted ->
                        if (!isGranted) {
                            // Handle permission denial (e.g., show a message or navigate back)
                            navController.popBackStack()
                        }
                    }
                )

                // Abstracted permission request mechanism
                val permissionRequestLauncher: (callback: (Boolean) -> Unit) -> PermissionRequestLauncher = { callback ->
                    object : PermissionRequestLauncher {
                        override fun launch(permission: String) {
                            cameraPermissionLauncher.launch(permission)
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
}