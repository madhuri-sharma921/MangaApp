package com.example.mangaverseapp.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


@Entity(tableName = "mangas")
data class MangaModel(
    @PrimaryKey
    val id: String,
    val title: String,
    val sub_title: String?,
    val status: String,
    val thumb: String,
    val summary: String,
    val authors: List<String>?,
    val genres: List<String>?,
    val nsfw: Boolean,
    val type: String,
    val total_chapter: Int,
    val create_at: Long,
    val update_at: Long,
    val lastUpdated: Long = System.currentTimeMillis()
)
