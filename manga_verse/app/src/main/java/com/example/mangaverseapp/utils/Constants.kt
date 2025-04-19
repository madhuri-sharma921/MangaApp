package com.example.mangaverseapp.utils



object Constants {
    // Base URL for the manga API
    const val BASE_URL = "https://mangaverse-api.p.rapidapi.com/"

    // Default page size for pagination
    const val DEFAULT_PAGE_SIZE = 20

    // Maximum cache duration in milliseconds (15 minutes)
    const val CACHE_DURATION = 15 * 60 * 1000L

    // Shared preferences name
    const val PREFS_NAME = "manga_verse_prefs"
}