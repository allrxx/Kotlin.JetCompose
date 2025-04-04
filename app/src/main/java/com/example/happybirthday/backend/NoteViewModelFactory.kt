package com.example.happybirthday.backend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.happybirthday.getCurrentUserId

class NoteViewModelFactory(
    private val repository: NoteRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val userId = getCurrentUserId()
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteViewModel(repository, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}