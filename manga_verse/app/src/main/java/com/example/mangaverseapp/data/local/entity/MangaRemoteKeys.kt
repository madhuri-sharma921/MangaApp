package com.example.mangaverseapp.data.local.entity



import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "manga_remote_keys")
data class MangaRemoteKeys(

    @PrimaryKey
    val mangaId: String,


    val prevKey: Int?,


    val nextKey: Int?
)