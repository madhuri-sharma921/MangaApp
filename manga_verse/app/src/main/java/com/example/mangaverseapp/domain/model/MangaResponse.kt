package com.example.mangaverseapp.domain.model

//
//data class MangaResponse(
//    val success: Boolean,
//    val data: List<MangaModel>,
//    val totalPages: Int,
//    val currentPage: Int
//)

data class MangaResponse(
    val code: Int,
    val data: List<MangaModel>,
    val currentPage: Int = 1,  // Add these fields or adjust based on actual API response
    val totalPages: Int = 1
)