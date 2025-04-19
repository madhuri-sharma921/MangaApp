package com.example.mangaverseapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mangaverseapp.domain.model.User



@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val email: String,
    val password: String,
    val isLoggedIn: Boolean = false
)