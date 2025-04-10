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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
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

    private val _errorFlow = MutableSharedFlow<String>()
    val errorFlow = _errorFlow.asSharedFlow()

    fun loadNotes() {
        val currentUserId = userIdSafe
        if (currentUserId == null) {
            Log.e("NoteViewModel", "Cannot load notes: User not authenticated.")
            _notes.value = emptyList()
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
                    _isLoading.value = false
                    _errorFlow.emit("Error loading notes: ${e.localizedMessage}")
                }
                .onCompletion { cause ->
                    Log.d("NoteViewModel", "Notes flow collection completed for $currentUserId. Cause: $cause") 
                }
                .collect { noteEntities ->
                    Log.d("NoteViewModel", "Collected ${noteEntities.size} note entities for $currentUserId")
                    _notes.value = noteEntities.map { it.toNoteItem() }
                    _isLoading.value = false
                    Log.d("NoteViewModel", "Notes state updated, isLoading set to false.")
                }
        }
    }

    fun insertNote(noteEntity: NoteEntity, onResult: (Result<Unit>) -> Unit) {
        val currentUserId = userIdSafe ?: run {
             val errorMsg = "Cannot insert note: User not authenticated."
             Log.e("NoteViewModel", errorMsg)
             onResult(Result.failure(IllegalStateException(errorMsg)))
             return
        }
        viewModelScope.launch {
            try {
                Log.d("NoteViewModel", "Inserting note ${noteEntity.id} for user $currentUserId")
                noteRepository.insertNote(noteEntity.copy(userId = currentUserId))
                Log.d("NoteViewModel", "Note ${noteEntity.id} insertion successful")
                onResult(Result.success(Unit))
            } catch (e: Exception) {
                val errorMsg = "Error inserting note ${noteEntity.id}: ${e.message}"
                Log.e("NoteViewModel", errorMsg, e)
                _errorFlow.emit(errorMsg)
                onResult(Result.failure(e))
            }
        }
    }

    fun updateNote(noteEntity: NoteEntity, onResult: (Result<Unit>) -> Unit) {
         val currentUserId = userIdSafe ?: run {
             val errorMsg = "Cannot update note: User not authenticated."
             Log.e("NoteViewModel", errorMsg)
              onResult(Result.failure(IllegalStateException(errorMsg)))
             return
        }
        viewModelScope.launch {
            try {
                 Log.d("NoteViewModel", "Updating note ${noteEntity.id} for user $currentUserId")
                noteRepository.updateNote(noteEntity.copy(userId = currentUserId))
                 Log.d("NoteViewModel", "Note ${noteEntity.id} update successful")
                 onResult(Result.success(Unit))
            } catch (e: Exception) {
                val errorMsg = "Error updating note ${noteEntity.id}: ${e.message}"
                Log.e("NoteViewModel", errorMsg, e)
                 _errorFlow.emit(errorMsg)
                 onResult(Result.failure(e))
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
            } catch (e: Exception) {
                val errorMsg = "Error deleting note $noteId: ${e.message}"
                Log.e("NoteViewModel", errorMsg, e)
                _errorFlow.emit(errorMsg)
            }
        }
    }
}