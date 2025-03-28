package com.example.happybirthday

import java.io.Serializable

data class NoteItem(
    val id: String = "",
    val text: String = "",
    val isExpanded: Boolean = false
) : Serializable // Added for Gson serialization