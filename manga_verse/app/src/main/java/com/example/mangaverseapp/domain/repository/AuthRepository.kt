package com.example.mangaverseapp.domain.repository



import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun isUserLoggedIn(): Flow<Boolean>
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun register(email: String, password: String, username: String): Result<Unit>
    suspend fun logout(): Result<Unit>
    fun getCurrentUserId(): String?
    fun getCurrentUserEmail(): String?
    suspend fun resetPassword(email: String): Result<Unit>
}