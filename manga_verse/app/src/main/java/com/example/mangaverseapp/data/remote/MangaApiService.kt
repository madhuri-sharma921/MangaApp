package com.example.mangaverseapp.data.remote

import com.example.mangaverseapp.domain.model.MangaResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface MangaApiService {
    @Headers(
        "X-RapidAPI-Key: bb622dd654msh69d1e771b4059b9p1df4a6jsn51cc3f6fbcff",
        "X-RapidAPI-Host: mangaverse-api.p.rapidapi.com"
    )
    @GET("manga/fetch")
    suspend fun fetchMangas(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("genres") genres: String? = "Harem,Fantasy",
        @Query("nsfw") nsfw: Boolean = true,
        @Query("type") type: String = "all"
    ): MangaResponse
}