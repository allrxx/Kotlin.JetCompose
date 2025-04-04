package com.example.happybirthday

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

private val auth: FirebaseAuth = Firebase.auth

fun signOutUser() {
    auth.signOut()

}

fun getCurrentUserId(): String {
    return auth.currentUser?.uid ?: ""
}
