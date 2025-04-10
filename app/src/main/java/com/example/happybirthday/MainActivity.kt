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
import com.example.happybirthday.backend.UserProfileRepository
import com.example.happybirthday.backend.ProfileViewModel
import com.example.happybirthday.backend.ProfileViewModelFactory
import com.example.happybirthday.ui.theme.AppTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {

    // Initialize Firebase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT

        // Create repositories from Room database
        val database = AppDatabase.getDatabase(this)
        val noteRepository = NoteRepository(database.noteDao())
        val userProfileRepository = UserProfileRepository(database.userProfileDao())

        // Create ViewModels using custom factories
        val noteViewModel: NoteViewModel by viewModels {
            NoteViewModelFactory(noteRepository)
        }
        val profileViewModel: ProfileViewModel by viewModels {
            ProfileViewModelFactory(userProfileRepository)
        }

        setContent {
            AppTheme {
                AppNavigation(noteViewModel, profileViewModel)
            }
        }
    }
}
