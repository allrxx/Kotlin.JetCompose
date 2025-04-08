package com.example.happybirthday.backend

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: NoteDao) {
    private val auth = FirebaseAuth.getInstance()

    private val userId: String
        get() = auth.currentUser?.uid ?: throw IllegalStateException("No authenticated user")

    fun getNotes(userId: String): Flow<List<NoteEntity>> {
        return noteDao.getNotesByUserId(userId)
    }

    suspend fun insertNote(note: NoteEntity) {
        try {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                Log.e("NoteRepository", "No authenticated user found")
                return
            }
            
            Log.d("NoteRepository", "Inserting note for user: ${currentUser.uid}")
            val noteWithUserId = note.copy(userId = currentUser.uid)
            noteDao.insertNote(noteWithUserId)
            Log.d("NoteRepository", "Note inserted successfully")
        } catch (e: Exception) {
            Log.e("NoteRepository", "Error inserting note: ${e.message}")
            throw e
        }
    }

    suspend fun updateNote(note: NoteEntity) {
        try {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                Log.e("NoteRepository", "No authenticated user found")
                return
            }
            
            Log.d("NoteRepository", "Updating note for user: ${currentUser.uid}")
            val noteWithUserId = note.copy(userId = currentUser.uid)
            noteDao.updateNote(noteWithUserId)
            Log.d("NoteRepository", "Note updated successfully")
        } catch (e: Exception) {
            Log.e("NoteRepository", "Error updating note: ${e.message}")
            throw e
        }
    }

    suspend fun deleteNoteById(noteId: String) {
        try {
            Log.d("NoteRepository", "Deleting note: $noteId")
            noteDao.deleteNoteById(noteId)
            Log.d("NoteRepository", "Note deleted successfully")
        } catch (e: Exception) {
            Log.e("NoteRepository", "Error deleting note: ${e.message}")
            throw e
        }
    }
}