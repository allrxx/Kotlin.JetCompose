package com.example.happybirthday.backend

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    // Get all notes for a user (all workspaces)
    @Query("SELECT * FROM notes WHERE userId = :userId ORDER BY timestamp DESC")
    fun getAllNotesForUser(userId: String): Flow<List<NoteEntity>>

    // Get notes for a specific workspace of a user
    @Query("SELECT * FROM notes WHERE userId = :userId AND workspaceId = :workspaceId ORDER BY timestamp DESC")
    fun getNotesForWorkspace(userId: String, workspaceId: Long): Flow<List<NoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity)

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Delete
    suspend fun deleteNote(note: NoteEntity)
}
