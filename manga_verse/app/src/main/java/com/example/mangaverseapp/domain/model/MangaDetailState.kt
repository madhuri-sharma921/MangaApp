package com.example.mangaverseapp.domain.model

sealed class MangaDetailState {
    object Loading : MangaDetailState()
    data class Success(val manga: MangaModel) : MangaDetailState()
    data class Error(val message: String) : MangaDetailState()
}