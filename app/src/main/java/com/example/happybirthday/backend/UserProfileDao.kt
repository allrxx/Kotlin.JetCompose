package com.example.happybirthday.backend

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {

    @Query("SELECT * FROM user_profiles WHERE userId = :userId")
    fun getUserProfile(userId: String): Flow<UserProfile?> // Use Flow to observe changes

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUserProfile(profile: UserProfile) // Insert or update

    @Query("SELECT EXISTS(SELECT 1 FROM user_profiles WHERE userId = :userId)")
    suspend fun profileExists(userId: String): Boolean
} 