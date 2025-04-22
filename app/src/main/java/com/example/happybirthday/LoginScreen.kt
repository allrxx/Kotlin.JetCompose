package com.example.happybirthday

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.happybirthday.backend.AuthResult
import com.example.happybirthday.backend.AuthViewModel
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import androidx.compose.foundation.BorderStroke

// Dark theme colors
val DarkBackground = Color(0xFF121212)
val DarkContainer = Color(0xFF1E1E1E)
val NeonGreen = Color(0xFF39FF14)

@Composable
fun LoginScreen(navController: NavController, onLoginSuccess: () -> Unit) {
    val authViewModel: AuthViewModel = viewModel()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var authError by remember { mutableStateOf<String?>(null) }

    val authResult by authViewModel.authResult.collectAsState(initial = AuthResult.Idle)

    LaunchedEffect(authResult) {
        when (authResult) {
            is AuthResult.Success -> {
                isLoading = false
                onLoginSuccess()
            }
            is AuthResult.Error -> {
                isLoading = false
                val ex = (authResult as AuthResult.Error).exception
                authError = when (ex) {
                    is FirebaseAuthInvalidUserException       -> "User not found"
                    is FirebaseAuthInvalidCredentialsException -> "Invalid credentials"
                    else                                      -> "Login failed: ${ex?.message}"
                }
            }
            AuthResult.Loading -> isLoading = true
            AuthResult.Idle    -> isLoading = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)  // dark overall background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Welcome Back!",
                style = MaterialTheme.typography.displaySmall,
                color = NeonGreen        // neon accent title
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Please sign in to continue",
                style = MaterialTheme.typography.bodyMedium,
                color = NeonGreen.copy(alpha = 0.8f)
            )
            Spacer(Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = DarkContainer  // dark card
                ),
                elevation = CardDefaults.cardElevation(8.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            authError = null
                        },
                        label = { Text("Email") },
                        leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null, tint = NeonGreen) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = NeonGreen,
                            unfocusedBorderColor = NeonGreen.copy(alpha = 0.5f),
                            focusedLabelColor = NeonGreen,
                            unfocusedLabelColor = NeonGreen.copy(alpha = 0.7f),
                            focusedContainerColor = DarkContainer,
                            unfocusedContainerColor = DarkContainer
                        )
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            authError = null
                        },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null, tint = NeonGreen) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = NeonGreen,
                            unfocusedBorderColor = NeonGreen.copy(alpha = 0.5f),
                            focusedLabelColor = NeonGreen,
                            unfocusedLabelColor = NeonGreen.copy(alpha = 0.7f),
                            focusedContainerColor = DarkContainer,
                            unfocusedContainerColor = DarkContainer
                        )
                    )
                    TextButton(
                        onClick = { /* TODO: Forgot password flow */ },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Forgot Password?", color = NeonGreen)
                    }
                    authError?.let { err ->
                        Text(
                            text = err,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Button(
                        onClick = { authViewModel.loginUser(email, password) },
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonGreen,
                            contentColor = Color.Black,
                            disabledContainerColor = NeonGreen.copy(alpha = 0.5f),
                            disabledContentColor = Color.Black.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.Black
                            )
                        } else {
                            Text("Login")
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    OutlinedButton(
                        onClick = { navController.navigate("registration") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        border = BorderStroke(1.dp, NeonGreen),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = NeonGreen
                        )
                    ) {
                        Text("Create Account")
                    }
                }
            }
        }
    }
}

