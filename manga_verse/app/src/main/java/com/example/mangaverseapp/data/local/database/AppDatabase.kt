package com.example.mangaverseapp.data.local


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

import com.example.mangaverseapp.data.local.dao.UserDao



import com.example.mangaverseapp.data.local.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
       // MangaEntity::class,
      //  RemoteKeys::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao


    companion object {
        const val DATABASE_NAME = "zenithra_db"
    }
}