package com.example.mangaverseapp.presentation.navigation



import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mangaverseapp.presentation.auth.SignInScreen
import com.example.mangaverseapp.presentation.auth.SignUpScreen
import com.example.mangaverseapp.presentation.auth.SignInViewModel
import com.example.mangaverseapp.presentation.auth.SignUpViewModel


@Composable
fun AuthNavigation(
    startDestination: String = AuthScreen.SignIn.route,
    navController: NavHostController = rememberNavController(),
    onAuthenticated: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        signInScreen(
            navController = navController,
            onAuthenticated = onAuthenticated
        )

        signUpScreen(
            navController = navController
        )
    }
}

fun NavGraphBuilder.signInScreen(
    navController: NavController,
    onAuthenticated: () -> Unit
) {
    composable(route = AuthScreen.SignIn.route) {
        val viewModel: SignInViewModel = hiltViewModel()

        SignInScreen(
            viewModel = viewModel,
            onNavigateToSignUp = {
                navController.navigate(AuthScreen.SignUp.route)
            },
            onSignInSuccess = onAuthenticated
        )
    }
}

fun NavGraphBuilder.signUpScreen(
    navController: NavController
) {
    composable(route = AuthScreen.SignUp.route) {
        val viewModel: SignUpViewModel = hiltViewModel()

        SignUpScreen(
            viewModel = viewModel,
            onNavigateBack = {
                navController.popBackStack()
            },
            onSignUpSuccess = {
                // Navigate back to the SignIn screen after successful registration
                navController.popBackStack()
            }
        )
    }
}

sealed class AuthScreen(val route: String) {
    object SignIn : AuthScreen("sign_in")
    object SignUp : AuthScreen("sign_up")
}