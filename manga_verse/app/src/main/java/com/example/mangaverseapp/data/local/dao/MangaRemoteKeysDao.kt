package com.example.mangaverseapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mangaverseapp.data.local.entity.MangaRemoteKeys

@Dao
interface MangaRemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKeys: List<MangaRemoteKeys>)

    @Query("SELECT * FROM manga_remote_keys WHERE mangaId = :mangaId")
    suspend fun getRemoteKeysByMangaId(mangaId: String): MangaRemoteKeys?

    @Query("DELETE FROM manga_remote_keys")
    suspend fun clearRemoteKeys()
}