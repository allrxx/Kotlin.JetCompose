package com.example.happybirthday

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.happybirthday.backend.NoteViewModel
import com.example.happybirthday.backend.toNoteEntity
import com.example.happybirthday.backend.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.gson.Gson
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map

// Define sealed class for auth state including Loading
sealed class AuthScreenState {
    object Loading : AuthScreenState()
    data class Authenticated(val user: FirebaseUser) : AuthScreenState()
    object Unauthenticated : AuthScreenState()
}

// Flow extension to map FirebaseUser to AuthScreenState
fun FirebaseAuth.authScreenStateFlow(): Flow<AuthScreenState> {
    return callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser)
        }
        addAuthStateListener(listener)
        awaitClose { removeAuthStateListener(listener) }
    }.map { user ->
        if (user != null) AuthScreenState.Authenticated(user) else AuthScreenState.Unauthenticated
    }
}

@Composable
fun AppNavigation(noteViewModel: NoteViewModel, profileViewModel: ProfileViewModel) {
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()
    
    // Use the new flow and collect the state
    val authState by auth.authScreenStateFlow().collectAsState(initial = AuthScreenState.Loading)

    // Start with loading screen
    NavHost(navController = navController, startDestination = "loading") {
        composable("loading") {
            LoadingScreen()
        }

        composable("login") {
            LoginScreen(
                navController = navController,
                onLoginSuccess = {
                    Log.d("NavGraph", "Login success, navigating to home")
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("registration") {
            RegistrationScreen(
                navController = navController,
                onLoginNavigate = { 
                    Log.d("NavGraph", "Navigating to login from registration")
                    navController.navigate("login") { 
                        popUpTo("registration") { inclusive = true }
                    } 
                },
                onRegistrationSuccess = {
                    Log.d("NavGraph", "Registration success, navigating to home")
                    navController.navigate("home") {
                        popUpTo("registration") { inclusive = true }
                    }
                }
            )
        }

        composable("home") {
            Log.d("NavGraph", "Composing home screen")
            HomeScreen(
                navController = navController,
                noteViewModel = noteViewModel,
                onLogout = {
                    Log.d("NavGraph", "Logging out, navigating to login")
                    auth.signOut()
                    navController.navigate("login") {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }

        // Add the Profile Screen destination
        composable("profile") {
            val currentAuthState = authState // Capture the state locally
            if (currentAuthState is AuthScreenState.Authenticated) {
                // Pass ProfileViewModel to ProfileScreen
                ProfileScreen(
                    navController = navController,
                    user = currentAuthState.user,
                    profileViewModel = profileViewModel // Pass ViewModel here
                )
            } else {
                // If somehow navigated here while unauthenticated, redirect to login
                Log.w("NavGraph", "User became unauthenticated in profile, navigating to login")
                LaunchedEffect(Unit) { // Use LaunchedEffect for side effects like navigation
                    navController.navigate("login") {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            }
        }

        composable(
            "editor/{noteJson}",
            arguments = listOf(navArgument("noteJson") { type = NavType.StringType })
        ) { backStackEntry ->
            if (authState is AuthScreenState.Authenticated) {
                val noteJson = backStackEntry.arguments?.getString("noteJson")
                val note = Gson().fromJson(noteJson, NoteItem::class.java)
                Log.d("NavGraph", "Showing editor for note: ${note.id}")
                EditorScreen(
                    navController = navController,
                    note = note,
                    onSave = { updatedNote ->
                        Log.d("NavGraph", "Saving note: ${updatedNote.id}")
                        val userId = (authState as AuthScreenState.Authenticated).user.uid
                        val noteEntity = updatedNote.toNoteEntity(userId = userId)
                        if (note.text.isEmpty()) {
                            Log.d("NavGraph", "Inserting new note: ${noteEntity.id}")
                            noteViewModel.insertNote(noteEntity)
                        } else {
                            Log.d("NavGraph", "Updating existing note: ${noteEntity.id}")
                            noteViewModel.updateNote(noteEntity)
                        }
                    }
                )
            } else {
                Log.w("NavGraph", "User became unauthenticated in editor, navigating to login")
                navController.navigate("login") {
                    popUpTo(navController.graph.id) { inclusive = true }
                }
            }
        }
    }

    // Effect to navigate based on auth state changes
    LaunchedEffect(authState) {
        when (authState) {
            is AuthScreenState.Loading -> {
                // Initial state, no need to navigate
            }
            is AuthScreenState.Authenticated -> {
                if (navController.currentDestination?.route != "home" && 
                    navController.currentDestination?.route?.startsWith("editor/") != true) {
                    Log.d("NavGraph", "Authenticated state detected, navigating to home")
                    navController.navigate("home") { 
                        popUpTo("loading") { inclusive = true }
                    }
                }
            }
            is AuthScreenState.Unauthenticated -> {
                if (navController.currentDestination?.route != "login" && 
                    navController.currentDestination?.route != "registration") {
                    Log.d("NavGraph", "Unauthenticated state detected, navigating to login")
                    navController.navigate("login") { 
                        popUpTo("loading") { inclusive = true }
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}