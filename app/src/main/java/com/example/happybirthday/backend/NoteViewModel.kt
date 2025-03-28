package com.example.happybirthday.backend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.happybirthday.getCurrentUserId
import kotlinx.coroutines.flow.Flow // Add this import
import kotlinx.coroutines.launch

class NoteViewModel(private val repository: NoteRepository, userId1: String) : ViewModel() {
    // Example: Fetch notes for the specific user using userId
    private val userId: String = getCurrentUserId() ?: throw IllegalStateException("User not logged in")
    val allNotes: Flow<List<NoteEntity>> = repository.getAllNotesForUser(userId)

    // Public getter for userId
    fun getUserId(): String = userId

    fun insertNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.insert(note)
        }
    }

    fun updateNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.update(note)
        }
    }

    fun delete(note: NoteEntity) {
        viewModelScope.launch {
            repository.delete(note)
        }
    }
}