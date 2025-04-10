package com.example.happybirthday

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.happybirthday.backend.NoteViewModel
import com.example.happybirthday.backend.toNoteEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import java.util.UUID
import android.util.Log

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
                        Text("My Notes")
                        currentUser?.email?.let { email ->
                            Text(
                                text = email,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = {
                        // Navigate to the profile screen
                        navController.navigate("profile")
                    }) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile"
                        )
                    }
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Sign out"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val newNote = NoteItem()
                    val noteJson = Gson().toJson(newNote)
                    navController.navigate("editor/$noteJson") {
                        launchSingleTop = true
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Note")
            }
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Adaptive(minSize = 160.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalItemSpacing = 8.dp,
                modifier = Modifier.padding(paddingValues)
            ) {
                items(
                    items = notes,
                    key = { noteItem -> noteItem.id }
                ) { noteItem ->
                    NoteEditorItem(
                        note = noteItem,
                        onUpdate = { updatedNote ->
                            // Currently unused in this simplified item
                        },
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



