package com.example.happybirthday

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.happybirthday.backend.ProfileViewModel
import com.example.happybirthday.backend.SaveState
import com.google.firebase.auth.FirebaseUser
import android.util.Log

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    user: FirebaseUser?,
    profileViewModel: ProfileViewModel
) {
    val name by profileViewModel.name.collectAsState()
    val username by profileViewModel.username.collectAsState()
    val dateOfBirth by profileViewModel.dateOfBirth.collectAsState()
    val gender by profileViewModel.gender.collectAsState()
    val isLoading by profileViewModel.isLoading.collectAsState()
    val saveState by profileViewModel.saveState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(user?.uid) {
        val userId = user?.uid
        if (userId != null) {
            Log.d("ProfileScreen", "LaunchedEffect triggered: Loading profile for $userId")
            profileViewModel.loadProfile(userId)
        } else {
            Log.w("ProfileScreen", "LaunchedEffect triggered: User is null, clearing profile.")
            profileViewModel.clearProfile()
        }
    }

    LaunchedEffect(saveState) {
        when (val state = saveState) {
            is SaveState.Success -> {
                snackbarHostState.showSnackbar(
                    message = "Profile saved successfully!",
                    duration = SnackbarDuration.Short
                )
            }
            is SaveState.Error -> {
                snackbarHostState.showSnackbar(
                    message = "Error saving profile: ${state.message}",
                    duration = SnackbarDuration.Long
                )
            }
            else -> { /* Idle or Saving */ }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        modifier = Modifier.systemBarsPadding()
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (user != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Email: ${user.email ?: "N/A"}")
                    Text("UID: ${user.uid}")

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { profileViewModel.onNameChange(it) },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = username,
                        onValueChange = { profileViewModel.onUsernameChange(it) },
                        label = { Text("Username") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = dateOfBirth,
                        onValueChange = { profileViewModel.onDateOfBirthChange(it) },
                        label = { Text("Date of Birth (YYYY-MM-DD)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = gender,
                        onValueChange = { profileViewModel.onGenderChange(it) },
                        label = { Text("Gender") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = { profileViewModel.saveProfile() },
                        enabled = saveState != SaveState.Saving,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    ) {
                        if (saveState == SaveState.Saving) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Save Profile")
                        }
                    }
                }
            } else {
                Text("User not available.", modifier = Modifier.align(Alignment.Center))
            }
        }
    }
} 