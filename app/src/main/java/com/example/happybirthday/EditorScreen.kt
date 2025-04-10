package com.example.happybirthday

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.happybirthday.backend.NoteViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    navController: NavController,
    note: NoteItem,
    noteViewModel: NoteViewModel,
    onSave: (updatedNote: NoteItem, callback: (Result<Unit>) -> Unit) -> Unit
) {
    var noteTitle by remember { mutableStateOf(note.title) }
    var noteText by remember { mutableStateOf(note.text) }
    val currentUser = FirebaseAuth.getInstance().currentUser
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy, hh:mm a", Locale.getDefault()) }

    var isSaving by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        noteViewModel.errorFlow.collect { errorMessage ->
            snackbarHostState.showSnackbar(
                message = errorMessage,
                duration = SnackbarDuration.Long
            )
            isSaving = false
        }
    }

    Scaffold(
        modifier = Modifier.systemBarsPadding(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Edited: ${dateFormat.format(Date(note.updatedAt))}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        Log.d("EditorScreen", "Back button clicked - potentially saving draft?")
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (isSaving) return@IconButton

                            Log.d("EditorScreen", "Save button clicked")
                            if (currentUser != null) {
                                val updatedNote = note.copy(
                                    title = noteTitle.trim(),
                                    text = noteText.trim()
                                )
                                if (updatedNote.title.isNotEmpty() || updatedNote.text.isNotEmpty() || note.id == updatedNote.id) {
                                    isSaving = true
                                    Log.d("EditorScreen", "Calling onSave for note: ${updatedNote.id}")
                                    onSave(updatedNote) { result ->
                                        scope.launch {
                                            isSaving = false
                                            if (result.isSuccess) {
                                                Log.d("EditorScreen", "Save successful, navigating back.")
                                                navController.popBackStack()
                                            } else {
                                                Log.e("EditorScreen", "Save failed", result.exceptionOrNull())
                                                snackbarHostState.showSnackbar(
                                                    message = "Save failed: ${result.exceptionOrNull()?.localizedMessage ?: "Unknown error"}",
                                                    duration = SnackbarDuration.Long
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    Log.d("EditorScreen", "Not saving empty new note, navigating back.")
                                    navController.popBackStack()
                                }
                            } else {
                                Log.e("EditorScreen", "No authenticated user found")
                                navController.navigate("login") { popUpTo(0) { inclusive = true } }
                            }
                        },
                        enabled = !isSaving
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Save Note",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            BasicTextField(
                value = noteTitle,
                onValueChange = { noteTitle = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                textStyle = MaterialTheme.typography.headlineMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                ),
                singleLine = true,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                decorationBox = { innerTextField ->
                    Box {
                        if (noteTitle.isEmpty()) {
                            Text(
                                text = "Title",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        innerTextField()
                    }
                }
            )

            BasicTextField(
                value = noteText,
                onValueChange = { noteText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 4.dp),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                decorationBox = { innerTextField ->
                    Box {
                        if (noteText.isEmpty()) {
                            Text(
                                text = "Note",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }
    }
}
