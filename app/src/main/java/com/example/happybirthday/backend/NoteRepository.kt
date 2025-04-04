package com.example.happybirthday.backend

import com.example.happybirthday.getCurrentUserId

class NoteRepository(private val noteDao: NoteDao) {

    private val userId: String
        get() = getCurrentUserId()

    fun getAllNotesForUser(userId: String) = noteDao.getAllNotesForUser(userId)

    suspend fun insert(note: NoteEntity) {
        noteDao.insertNote(note.copy(userId = userId).withUpdatedTimestamp())
    }

    suspend fun update(note: NoteEntity) {
        noteDao.updateNote(note.copy(userId = userId).withUpdatedTimestamp())
    }

    suspend fun delete(note: NoteEntity) {
        noteDao.deleteNote(note)
    }
}