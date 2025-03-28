package com.example.happybirthday.backend

import com.example.happybirthday.NoteItem

// Convert NoteEntity to NoteItem for UI
fun NoteEntity.toNoteItem() = NoteItem(
    id = id,
    text = text,
    isExpanded = isExpanded
)

// Convert NoteItem to NoteEntity for DB, with userId and workspaceId
fun NoteItem.toNoteEntity(userId: String, workspaceId: Long = 0L) = NoteEntity(
    id = id,
    userId = userId,
    workspaceId = workspaceId,
    text = text,
    isExpanded = isExpanded
)