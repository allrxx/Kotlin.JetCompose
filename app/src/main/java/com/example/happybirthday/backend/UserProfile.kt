package com.example.happybirthday.backend

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey
    val userId: String, // Using Firebase UID as the primary key
    val name: String? = null,
    val username: String? = null,
    val dateOfBirth: String? = null, // Storing as String for simplicity, consider Long for timestamp or proper Date type
    val gender: String? = null
) 