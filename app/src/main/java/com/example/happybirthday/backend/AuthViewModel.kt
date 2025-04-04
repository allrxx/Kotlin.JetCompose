package com.example.happybirthday.backend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class AuthResult {
    object Idle : AuthResult()        // No authentication in progress
    object Loading : AuthResult()     // Authentication in progress
    data class Success(val user: FirebaseUser?) : AuthResult() // Login successful
    data class Error(val exception: Exception?) : AuthResult() // Login failed
}

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _authResult = MutableStateFlow<AuthResult>(AuthResult.Idle)
    val authResult: StateFlow<AuthResult> = _authResult

    fun registerUser(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authResult.value = AuthResult.Loading
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                _authResult.value = AuthResult.Success(result.user)
            } catch (e: Exception) {
                _authResult.value = AuthResult.Error(e)
            }
        }
    }

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authResult.value = AuthResult.Loading // Set to Loading when login starts
                val result = auth.signInWithEmailAndPassword(email, password).await()
                _authResult.value = AuthResult.Success(result.user)
            } catch (e: Exception) {
                _authResult.value = AuthResult.Error(e)
            }
        }
    }

}