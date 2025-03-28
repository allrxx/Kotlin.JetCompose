package com.example.happybirthday

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

private val auth: FirebaseAuth = Firebase.auth

fun signInUser(
    email: String,
    password: String,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit
) {
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onSuccess()
            } else {
                task.exception?.let { onFailure(it) }
            }
        }
}

fun registerUser(
    email: String,
    password: String,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit
) {
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onSuccess()
            } else {
                task.exception?.let { onFailure(it) }
            }
        }
}

fun signOutUser() {
    auth.signOut()
}

fun getCurrentUserId(): String {
    return auth.currentUser?.uid ?: ""
}

fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun validateCredentials(email: String, password: String): String? {
    return when {
        email.isEmpty() -> "Email cannot be empty"
        !isValidEmail(email) -> "Invalid email format"
        password.isEmpty() -> "Password cannot be empty"
        password.length < 6 -> "Password must be at least 6 characters"
        else -> null
    }
}
