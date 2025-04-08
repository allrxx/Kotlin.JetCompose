package com.example.happybirthday

import java.io.Serializable
import java.util.UUID

data class NoteItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val text: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) : Serializable // Added for Gson serialization