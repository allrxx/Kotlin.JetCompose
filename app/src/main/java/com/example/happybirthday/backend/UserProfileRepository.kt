package com.example.happybirthday.backend

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileRepository @Inject constructor(private val userProfileDao: UserProfileDao) {

    fun getUserProfile(userId: String): Flow<UserProfile?> {
        return userProfileDao.getUserProfile(userId)
    }

    suspend fun saveUserProfile(profile: UserProfile) {
        userProfileDao.upsertUserProfile(profile)
    }

    suspend fun profileExists(userId: String): Boolean {
        return userProfileDao.profileExists(userId)
    }
} 