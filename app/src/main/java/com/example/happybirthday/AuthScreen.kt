package com.example.happybirthday

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun AuthScreen(
    navController: NavController,
    onAuthSuccess: () -> Unit,
    onAuthNeeded: () -> Unit
) {
    val auth = Firebase.auth

    LaunchedEffect(auth.currentUser) {
        if (auth.currentUser != null) {
            onAuthSuccess()
        } else {
            onAuthNeeded()
        }
    }
}