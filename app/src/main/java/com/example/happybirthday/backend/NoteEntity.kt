package com.example.happybirthday.backend

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey val id: String, // UUID or Firebase-generated ID
    val userId: String,         // Firebase Auth UID, required
    val workspaceId: Long,      // Workspace identifier
    val text: String = "",      // Note content
    val isExpanded: Boolean = false, // UI state
    val timestamp: Long = System.currentTimeMillis() // Creation or last update time
) {
    // Optional: Helper to update timestamp on modification
    fun withUpdatedTimestamp(): NoteEntity {
        return copy(timestamp = System.currentTimeMillis())
    }
}