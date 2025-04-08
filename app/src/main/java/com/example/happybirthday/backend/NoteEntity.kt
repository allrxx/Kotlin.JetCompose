package com.example.happybirthday.backend

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.happybirthday.NoteItem
import java.util.UUID

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val text: String = "",
    val userId: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

fun NoteEntity.toNoteItem(): NoteItem = NoteItem(
    id = id,
    title = title,
    text = text,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun NoteItem.toNoteEntity(userId: String): NoteEntity = NoteEntity(
    id = id,
    title = title,
    text = text,
    userId = userId,
    createdAt = createdAt,
    updatedAt = updatedAt
)