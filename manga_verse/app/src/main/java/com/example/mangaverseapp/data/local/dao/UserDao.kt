package com.example.mangaverseapp.data.local.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mangaverseapp.data.local.entity.UserEntity

import kotlinx.coroutines.flow.Flow



@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUserById(id: Long): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE isLoggedIn = 1 LIMIT 1")
    fun getLoggedInUser(): Flow<UserEntity?>

    @Query("UPDATE users SET isLoggedIn = 1 WHERE id = :userId")
    suspend fun setUserLoggedIn(userId: Int)

    @Query("UPDATE users SET isLoggedIn = 0")
    suspend fun logoutAllUsers()
}
