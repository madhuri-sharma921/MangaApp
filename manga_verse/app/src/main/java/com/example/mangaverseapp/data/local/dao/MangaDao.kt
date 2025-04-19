package com.example.mangaverseapp.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mangaverseapp.domain.model.MangaModel
import kotlinx.coroutines.flow.Flow

@Dao
interface MangaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(mangas: List<MangaModel>)

    @Query("SELECT * FROM mangas ORDER BY lastUpdated DESC")
    fun getMangasPagingSource(): PagingSource<Int, MangaModel>

    @Query("SELECT * FROM mangas WHERE id = :mangaId")
    fun getMangaById(mangaId: String): Flow<MangaModel?>

    @Query("DELETE FROM mangas")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM mangas")
    suspend fun getMangaCount(): Int
}
