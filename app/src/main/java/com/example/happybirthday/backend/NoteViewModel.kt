package com.example.happybirthday.backend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.happybirthday.NoteItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.collect

class NoteViewModel(private val noteRepository: NoteRepository) : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    
    private val userId: String
        get() = auth.currentUser?.uid ?: throw IllegalStateException("No authenticated user")

    private val _notes = MutableStateFlow<List<NoteItem>>(emptyList())
    val notes: StateFlow<List<NoteItem>> = _notes.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadNotes() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                noteRepository.getNotes(userId).collect { noteEntities ->
                    _notes.value = noteEntities.map { it.toNoteItem() }
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun insertNote(noteEntity: NoteEntity) {
        viewModelScope.launch {
            noteRepository.insertNote(noteEntity)
            loadNotes()
        }
    }

    fun updateNote(noteEntity: NoteEntity) {
        viewModelScope.launch {
            noteRepository.updateNote(noteEntity)
            loadNotes()
        }
    }

    fun deleteNoteById(noteId: String) {
        viewModelScope.launch {
            noteRepository.deleteNoteById(noteId)
            loadNotes()
        }
    }
}