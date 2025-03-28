package com.example.happybirthday

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.happybirthday.backend.NoteViewModel
import com.example.happybirthday.backend.toNoteEntity
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.gson.Gson

@Composable
fun AppNavigation(noteViewModel: NoteViewModel) {
    val navController = rememberNavController()
    val auth = Firebase.auth
    val isLoggedIn = auth.currentUser != null

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) "home" else "login"
    ) {
        composable("login") {
            LoginScreen(navController = navController)
        }

        composable("home") {
            if (isLoggedIn) {
                HomeScreen(navController, noteViewModel)
            } else {
                navController.navigate("login") {
                    popUpTo(navController.graph.id) { inclusive = true }
                }
            }
        }

        composable(
            "editor/{noteJson}",
            arguments = listOf(navArgument("noteJson") { type = NavType.StringType })
        ) { backStackEntry ->
            if (isLoggedIn) {
                val noteJson = backStackEntry.arguments?.getString("noteJson")
                val note = Gson().fromJson(noteJson, NoteItem::class.java)
                EditorScreen(
                    navController = navController,
                    note = note,
                    onSave = { updatedNote ->
                        val userId = auth.currentUser?.uid ?: run {
                            navController.navigate("login")
                            return@EditorScreen
                        }

                        val noteEntity = updatedNote.toNoteEntity(
                            userId = userId,
                            workspaceId = 0L
                        )
                        noteViewModel.insertNote(noteEntity)
                        navController.popBackStack()
                    }
                )
            } else {
                navController.navigate("login") {
                    popUpTo(navController.graph.id) { inclusive = true }
                }
            }
        }
    }
}