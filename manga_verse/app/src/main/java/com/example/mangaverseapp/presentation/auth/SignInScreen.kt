package com.example.mangaverseapp.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.mangaverseapp.R
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mangaverseapp.presentation.common.LoadingState
import com.example.mangaverseapp.presentation.common.SocialSignInButton
import com.example.mangaverseapp.presentation.common.ZenithraButton
import com.example.mangaverseapp.presentation.common.ZenithraTextField

@Composable
fun SignInScreen(
    viewModel: SignInViewModel,
    onNavigateToSignUp: () -> Unit,
    onSignInSuccess: () -> Unit
) {
    val signInState by viewModel.signInState.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    // Check if user is already logged in
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            onSignInSuccess()
        }
    }

    // Handle sign-in state changes
    LaunchedEffect(signInState) {
        when (signInState) {
            is SignInState.Success -> {
                onSignInSuccess()
                viewModel.resetState()
            }
            is SignInState.Error -> {
                snackbarHostState.showSnackbar(
                    (signInState as SignInState.Error).message
                )
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (signInState is SignInState.Loading) {
            LoadingState()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(60.dp))

                Text(
                    text = "Zenithra",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 16.sp
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Welcome back",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp
                    )
                )
                Text(
                    text = "Please enter your details to signin",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Thin,
                        fontSize = 10.sp
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Social Sign In Buttons
                Row(
                    modifier = Modifier
                        .wrapContentWidth() // Only take the space needed by the buttons
                        .padding(horizontal = 4.dp), // Minimal padding
                    horizontalArrangement = Arrangement.Center, // Center the buttons
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SocialSignInButton(
                        icon = painterResource(id = R.drawable.google_logo),
                        onClick = { },
                        modifier = Modifier.padding(horizontal = 4.dp) // Very small padding between buttons
                    )

                    SocialSignInButton(
                        icon = painterResource(id = R.drawable.apple_logo),
                        onClick = { },
                        modifier = Modifier.padding(horizontal = 4.dp) // Very small padding between buttons
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))

                // Or divider
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Divider(modifier = Modifier.weight(1f))
                    Text(
                        text = "OR",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Divider(modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Email field
                ZenithraTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password field
                ZenithraTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                painter = painterResource(
                                    id = if (passwordVisible) R.drawable.visibility_off
                                    else R.drawable.visibility
                                ),
                                contentDescription = if (passwordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Forgot Password
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    TextButton(onClick = { /* TODO: Implement forgot password */ }) {
                        Text(
                            text = "Forgot Password?",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Sign In Button
                ZenithraButton(
                    text = "Sign In",
                    onClick = {
                        viewModel.signIn(email, password)
                    },
                    isLoading = signInState is SignInState.Loading
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Sign Up prompt
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Don't have an account yet?",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    TextButton(onClick = onNavigateToSignUp) {
                        Text(
                            text = "Sign Up",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}