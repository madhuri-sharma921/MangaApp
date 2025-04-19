package com.example.mangaverseapp.domain.repository

import com.example.mangaverseapp.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun signIn(email: String, password: String): Result<User>
    suspend fun createUser(email: String, password: String): Result<User>
    fun getCurrentUser(): Flow<User?>
    suspend fun signOut()
}