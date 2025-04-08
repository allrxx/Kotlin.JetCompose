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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart

class NoteViewModel(private val noteRepository: NoteRepository) : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    
    private val userIdSafe: String?
        get() = auth.currentUser?.uid

    private val _notes = MutableStateFlow<List<NoteItem>>(emptyList())
    val notes: StateFlow<List<NoteItem>> = _notes.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadNotes() {
        val currentUserId = userIdSafe
        if (currentUserId == null) {
            Log.e("NoteViewModel", "Cannot load notes: User not authenticated.")
            _notes.value = emptyList() // Clear notes if user logs out
            _isLoading.value = false
            return
        }
        
        Log.d("NoteViewModel", "loadNotes called for user: $currentUserId")
        viewModelScope.launch {
            noteRepository.getNotes(currentUserId)
                .onStart { 
                    Log.d("NoteViewModel", "Starting notes flow collection for $currentUserId")
                    _isLoading.value = true 
                }
                .catch { e -> 
                    Log.e("NoteViewModel", "Error in notes flow for $currentUserId: ${e.message}", e)
                    _isLoading.value = false // Stop loading on flow error
                }
                .onCompletion { cause ->
                    // This might log when the scope is cancelled, not necessarily on normal completion for Room Flow
                    Log.d("NoteViewModel", "Notes flow collection completed for $currentUserId. Cause: $cause") 
                    _isLoading.value = false // Ensure loading is false when flow completes/cancels
                }
                .collect { noteEntities ->
                    Log.d("NoteViewModel", "Collected ${noteEntities.size} note entities for $currentUserId")
                    _notes.value = noteEntities.map { it.toNoteItem() }
                    Log.d("NoteViewModel", "Notes state updated. Setting isLoading=false")
                    _isLoading.value = false // Set loading false after first (or subsequent) emission
                }
        }
    }

    fun insertNote(noteEntity: NoteEntity) {
        val currentUserId = userIdSafe ?: run {
             Log.e("NoteViewModel", "Cannot insert note: User not authenticated.")
             return
        }
        viewModelScope.launch {
            try {
                Log.d("NoteViewModel", "Inserting note ${noteEntity.id} for user $currentUserId")
                // Ensure the userId is set correctly before inserting
                noteRepository.insertNote(noteEntity.copy(userId = currentUserId))
                Log.d("NoteViewModel", "Note ${noteEntity.id} insertion successful")
                // No need to call loadNotes() - Flow should update automatically
            } catch (e: Exception) {
                Log.e("NoteViewModel", "Error inserting note ${noteEntity.id}: ${e.message}", e)
            }
        }
    }

    fun updateNote(noteEntity: NoteEntity) {
         val currentUserId = userIdSafe ?: run {
             Log.e("NoteViewModel", "Cannot update note: User not authenticated.")
             return
        }
        viewModelScope.launch {
            try {
                 Log.d("NoteViewModel", "Updating note ${noteEntity.id} for user $currentUserId")
                 // Ensure the userId is set correctly before updating
                noteRepository.updateNote(noteEntity.copy(userId = currentUserId))
                 Log.d("NoteViewModel", "Note ${noteEntity.id} update successful")
                // No need to call loadNotes() - Flow should update automatically
            } catch (e: Exception) {
                Log.e("NoteViewModel", "Error updating note ${noteEntity.id}: ${e.message}", e)
            }
        }
    }

    fun deleteNoteById(noteId: String) {
        val currentUserId = userIdSafe ?: run {
             Log.e("NoteViewModel", "Cannot delete note: User not authenticated.")
             return
        }
        viewModelScope.launch {
            try {
                Log.d("NoteViewModel", "Deleting note $noteId for user $currentUserId")
                noteRepository.deleteNoteById(noteId)
                 Log.d("NoteViewModel", "Note $noteId deletion successful")
                // No need to call loadNotes() - Flow should update automatically
            } catch (e: Exception) {
                Log.e("NoteViewModel", "Error deleting note $noteId: ${e.message}", e)
            }
        }
    }
}