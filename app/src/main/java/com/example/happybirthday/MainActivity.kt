package com.example.happybirthday

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.core.view.WindowCompat
import com.example.happybirthday.backend.AppDatabase
import com.example.happybirthday.backend.NoteRepository
import com.example.happybirthday.backend.NoteViewModel
import com.example.happybirthday.backend.NoteViewModelFactory
import com.example.happybirthday.ui.theme.AppTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    // Initialize Firebase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT

        // Retrieve the Firebase UID (or handle null appropriately)
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: "defaultUser"

        // Create repository from Room database
        val database = AppDatabase.getInstance(this)
        val repository = NoteRepository(database.noteDao())

        // Create NoteViewModel using custom factory
        val noteViewModel: NoteViewModel by viewModels {
            NoteViewModelFactory(repository)
        }

        setContent {
            AppTheme {
                AppNavigation(noteViewModel)
            }
        }
    }
}
