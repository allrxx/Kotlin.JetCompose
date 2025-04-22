package com.example.happybirthday

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.happybirthday.backend.NoteViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import android.util.Log

val AddNoteFabColor = Color(0xFFFF4081)           // unique pink
val AddNoteFabContentColor = Color.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, noteViewModel: NoteViewModel, onLogout: () -> Unit) {
    val notes by noteViewModel.notes.collectAsState()
    val isLoading by noteViewModel.isLoading.collectAsState()
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    LaunchedEffect(Unit) {
        auth.currentUser?.uid?.let {
            noteViewModel.loadNotes()
        } ?: run {
            Log.e("HomeScreen", "No authenticated user found, cannot load notes")
            onLogout()
        }
    }
    
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection).systemBarsPadding(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "NotePad",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        currentUser?.email?.let { email ->
                            Text(
                                text = email,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                },
                actions = {
                    FilledIconButton(
                        onClick = { navController.navigate("profile") },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Profile")
                    }
                    FilledIconButton(
                        onClick = onLogout,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Sign out")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        // Center the FAB and apply custom colors
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val newNote = NoteItem()
                    val noteJson = Gson().toJson(newNote)
                    navController.navigate("editor/$noteJson") {
                        launchSingleTop = true
                    }
                },
                containerColor = AddNoteFabColor,
                contentColor = AddNoteFabContentColor
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Note")
            }
        },
        floatingActionButtonPosition = FabPosition.Center,  // Center position
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp), // Adjusted padding
                verticalArrangement = Arrangement.spacedBy(12.dp), // Spacing between list items
                modifier = Modifier.fillMaxSize().padding(paddingValues) // Use fillMaxSize for list
            ) {
                items(
                    items = notes,
                    key = { noteItem -> noteItem.id }
                ) { noteItem ->
                    NoteEditorItem(
                        note = noteItem,
                        onDelete = {
                            Log.d("HomeScreen", "Deleting note: ${noteItem.id}")
                            noteViewModel.deleteNoteById(noteItem.id)
                        },
                        onClick = {
                            val noteJson = Gson().toJson(noteItem)
                            navController.navigate("editor/$noteJson")
                        }
                    )
                }
            }
        }
    }
}



