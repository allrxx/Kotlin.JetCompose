package com.example.happybirthday

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.happybirthday.backend.NoteViewModel
import com.example.happybirthday.backend.toNoteEntity
import com.example.happybirthday.backend.toNoteItem
import com.google.gson.Gson
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, noteViewModel: NoteViewModel,onLogout: () -> Unit ) {
    val notes by noteViewModel.allNotes.collectAsState(initial = emptyList())

    Scaffold(
        modifier = Modifier.systemBarsPadding(),
        topBar = {
            TopAppBar(
                title = { Text("Notes") },
                actions = {
                    IconButton(
                        onClick = {
                            signOutUser()
                            onLogout()
                            // Navigate to login screen and clear back stack
                            navController.navigate("login") {
                                popUpTo(navController.graph.startDestinationId) {
                                    inclusive = true
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Sign out"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val newNote = NoteItem(
                        id = UUID.randomUUID().toString(),
                        text = ""
                    )
                    val noteJson = Gson().toJson(newNote)
                    navController.navigate("editor/$noteJson") {
                        launchSingleTop = true
                    }
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Note")
            }
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = paddingValues,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            items(
                items = notes,
                key = { noteEntity -> noteEntity.id }
            ) { noteEntity ->
                val noteItem = noteEntity.toNoteItem()
                NoteEditorItem(
                    note = noteItem,
                    onUpdate = { updatedNote ->
                        noteViewModel.updateNote(updatedNote.toNoteEntity(noteViewModel.getUserId()))
                    },
                    onDelete = {
                        noteViewModel.delete(noteEntity)
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