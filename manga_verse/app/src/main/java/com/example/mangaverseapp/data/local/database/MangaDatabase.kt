package com.example.mangaverseapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mangaverseapp.data.local.Converters
import com.example.mangaverseapp.data.local.dao.MangaDao
import com.example.mangaverseapp.data.local.dao.MangaRemoteKeysDao
import com.example.mangaverseapp.data.local.entity.MangaRemoteKeys
import com.example.mangaverseapp.domain.model.MangaModel
@Database(
    entities = [MangaModel::class, MangaRemoteKeys::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MangaDatabase : RoomDatabase() {
    abstract fun mangaDao(): MangaDao
    abstract fun remoteKeysDao(): MangaRemoteKeysDao
}

