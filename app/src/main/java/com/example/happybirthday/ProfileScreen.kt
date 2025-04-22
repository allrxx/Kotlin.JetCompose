package com.example.happybirthday

import androidx.compose.foundation.background // Import background
import androidx.compose.foundation.layout.*
// ...existing imports...
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
// Add necessary icon imports
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Wc // For gender
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush // Import Brush
import androidx.compose.ui.graphics.Color // Import Color
import androidx.compose.ui.graphics.compositeOver // Needed for compositing colors
import androidx.navigation.NavController
import com.example.happybirthday.backend.ProfileViewModel
import com.example.happybirthday.backend.SaveState
import com.google.firebase.auth.FirebaseUser
import android.util.Log

// Define some vibrant colors
val ProfileGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF6200EE), Color(0xFF03DAC5), Color(0xFFFFC107))
)
// Adjusted colors for dark text on white Card
val TextFieldBackgroundColor = Color.White.copy(alpha = 0.9f)
val TextFieldTextColor = Color.Black
val TextFieldLabelColor = Color.Gray
val TextFieldCursorColor = Color.Black
val TextFieldIconColor = Color.Gray
val ButtonBackgroundColor = Color(0xFFBB86FC)
val ButtonTextColor = Color.Black
// Define a semi-transparent color for the TopAppBar based on the gradient top
val TopAppBarBackgroundColor = Color(0xFF6200EE).copy(alpha = 0.5f)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    user: FirebaseUser?,
    profileViewModel: ProfileViewModel
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val name by profileViewModel.name.collectAsState()
    val username by profileViewModel.username.collectAsState()
    val dateOfBirth by profileViewModel.dateOfBirth.collectAsState()
    val gender by profileViewModel.gender.collectAsState()
    val isLoading by profileViewModel.isLoading.collectAsState()
    val saveState by profileViewModel.saveState.collectAsState()

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
                title = { Text("Edit Profile", color = Color.White) }, // Keep white title
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White // Keep white icon
                        )
                    }
                },
                // Use a semi-transparent background for better contrast
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TopAppBarBackgroundColor, // Use defined semi-transparent color
                    scrolledContainerColor = TopAppBarBackgroundColor // Keep consistent on scroll
                )
            )
        },
        // Apply gradient background to the main content area
        modifier = Modifier
            .fillMaxSize()
            .background(ProfileGradient) // Apply gradient here
            .systemBarsPadding() // Adjust padding for system bars
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Apply padding from Scaffold
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.White // Make indicator visible on gradient
                )
            } else if (user != null) {
                // Wrap content in a contrasting Card
                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Ensure user info text is white and clearly visible
                        Text(
                            "Email: ${user.email ?: "N/A"}",
                            color = TextFieldTextColor, // Use defined white color
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            "UID: ${user.uid}",
                            color = TextFieldTextColor.copy(alpha = 0.8f), // Slightly dimmer white
                            style = MaterialTheme.typography.bodySmall
                        )

                        Spacer(modifier = Modifier.height(24.dp)) // Increased spacer

                        // Use updated TextFieldBackgroundColor in colors definition
                        OutlinedTextField(
                            value = name,
                            onValueChange = { profileViewModel.onNameChange(it) },
                            label = { Text("Name") },
                            leadingIcon = { Icon(Icons.Filled.Person, contentDescription = "Name Icon", tint = TextFieldIconColor) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextFieldTextColor,
                                unfocusedTextColor = TextFieldTextColor,
                                focusedContainerColor = TextFieldBackgroundColor, // Updated alpha
                                unfocusedContainerColor = TextFieldBackgroundColor, // Updated alpha
                                cursorColor = TextFieldCursorColor,
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                                focusedLabelColor = TextFieldLabelColor,
                                unfocusedLabelColor = TextFieldLabelColor,
                            )
                        )
                        OutlinedTextField(
                            value = username,
                            onValueChange = { profileViewModel.onUsernameChange(it) },
                            label = { Text("Username") },
                            leadingIcon = { Icon(Icons.Filled.Face, contentDescription = "Username Icon", tint = TextFieldIconColor) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextFieldTextColor,
                                unfocusedTextColor = TextFieldTextColor,
                                focusedContainerColor = TextFieldBackgroundColor, // Updated alpha
                                unfocusedContainerColor = TextFieldBackgroundColor, // Updated alpha
                                cursorColor = TextFieldCursorColor,
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                                focusedLabelColor = TextFieldLabelColor,
                                unfocusedLabelColor = TextFieldLabelColor,
                            )
                        )
                        OutlinedTextField(
                            value = dateOfBirth,
                            onValueChange = { profileViewModel.onDateOfBirthChange(it) },
                            label = { Text("Date of Birth (YYYY-MM-DD)") },
                            leadingIcon = { Icon(Icons.Filled.CalendarToday, contentDescription = "DOB Icon", tint = TextFieldIconColor) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextFieldTextColor,
                                unfocusedTextColor = TextFieldTextColor,
                                focusedContainerColor = TextFieldBackgroundColor, // Updated alpha
                                unfocusedContainerColor = TextFieldBackgroundColor, // Updated alpha
                                cursorColor = TextFieldCursorColor,
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                                focusedLabelColor = TextFieldLabelColor,
                                unfocusedLabelColor = TextFieldLabelColor,
                            )
                        )
                        OutlinedTextField(
                            value = gender,
                            onValueChange = { profileViewModel.onGenderChange(it) },
                            label = { Text("Gender") },
                            leadingIcon = { Icon(Icons.Filled.Wc, contentDescription = "Gender Icon", tint = TextFieldIconColor) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextFieldTextColor,
                                unfocusedTextColor = TextFieldTextColor,
                                focusedContainerColor = TextFieldBackgroundColor, // Updated alpha
                                unfocusedContainerColor = TextFieldBackgroundColor, // Updated alpha
                                cursorColor = TextFieldCursorColor,
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                                focusedLabelColor = TextFieldLabelColor,
                                unfocusedLabelColor = TextFieldLabelColor,
                            )
                        )

                        Spacer(modifier = Modifier.weight(1f)) // Pushes button to bottom

                        // Styled Button
                        Button(
                            onClick = { profileViewModel.saveProfile() },
                            enabled = saveState != SaveState.Saving,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp), // Standard button height
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ButtonBackgroundColor,
                                contentColor = ButtonTextColor,
                                disabledContainerColor = ButtonBackgroundColor.copy(alpha = 0.5f), // Dim when disabled
                                disabledContentColor = ButtonTextColor.copy(alpha = 0.5f)
                            ),
                            shape = MaterialTheme.shapes.medium // Rounded corners
                        ) {
                            if (saveState == SaveState.Saving) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp,
                                    color = ButtonTextColor // Use button text color for indicator
                                )
                            } else {
                                Text("Save Profile", style = MaterialTheme.typography.labelLarge)
                            }
                        }
                    }
                }
            } else {
                Text(
                    "User not available.",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.White // Make text visible
                )
            }
        }
    }
}