package com.example.happybirthday

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.happybirthday.backend.NoteViewModel
import com.example.happybirthday.backend.toNoteEntity
import com.google.firebase.Firebase
import com.google.firebase.auth.*
import com.google.gson.Gson
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

@Composable
fun AppNavigation(noteViewModel: NoteViewModel) {
    val navController = rememberNavController()
    val auth = Firebase.auth

    // Collect the auth state properly
    val authState = auth.authStateChanged().collectAsState(initial = null)

    NavHost(
        navController = navController,
        startDestination = "auth"
    ) {
        composable("auth") {
            AuthScreen(
                onAuthSuccess = { navController.navigate("home") },
                onAuthNeeded = { navController.navigate("login") }
            )
        }

        composable("login") {
            LoginScreen(
                navController = navController,
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }

        composable("registration") {
            RegistrationScreen(
                navController = navController,
                onLoginNavigate = { navController.navigate("login") }
            )
        }

        composable("home") {
            HomeScreen(
                navController = navController,
                noteViewModel = noteViewModel,
                onLogout = {
                    navController.navigate("auth") {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }

        composable(
            "editor/{noteJson}",
            arguments = listOf(navArgument("noteJson") { type = NavType.StringType })
        ) { backStackEntry ->
            // Access the value directly instead of using delegate
            if (authState.value != null) {
                val noteJson = backStackEntry.arguments?.getString("noteJson")
                val note = Gson().fromJson(noteJson, NoteItem::class.java)
                EditorScreen(
                    navController = navController,
                    note = note,
                    onSave = { updatedNote ->
                        Log.d("EditorScreen", "onSave called, authState.value: ${authState.value}")
                        val userId = authState.value?.uid ?: run {
                            Log.w("EditorScreen", "User not authenticated, navigating to auth")
                            navController.navigate("auth")
                            return@EditorScreen
                        }
                        val noteEntity = updatedNote.toNoteEntity(userId = userId, workspaceId = 0L)
                        noteViewModel.insertNote(noteEntity)
                        navController.popBackStack()
                    }
                )
            } else {
                navController.navigate("auth") {
                    popUpTo(navController.graph.id) { inclusive = true }
                }
            }
        }
    }
}

fun FirebaseAuth.authStateChanged() = callbackFlow {
    val listener = FirebaseAuth.AuthStateListener { auth ->
        trySend(auth.currentUser)
    }
    addAuthStateListener(listener)
    awaitClose { removeAuthStateListener(listener) }
}