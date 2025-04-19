# MangaApp
Manga Verse Android Application
Project Overview
This Android application is built using modern Android development technologies and follows Clean Architecture principles with MVVM design pattern. The app provides user authentication, manga browsing, and real-time face recognition features.
Tech Stack

Language: Kotlin
UI Framework: Jetpack Compose
Architecture: Clean Architecture + MVVM
Navigation: Jetpack Navigation Component
Local Database: Room
Dependency Injection: Hilt
Network: Retrofit
Face Detection: MediaPipe
Asynchronous Programming: Kotlin Coroutines & Flow

Features

User Authentication

Secure login with Room database storage
Auto-login for existing users
Account creation for new users


Manga Browser

Fetch manga data from MangaVerse API
Offline caching with Room database
Pagination support
Detailed manga information screen


Face Recognition

Real-time face detection using MediaPipe
Live camera feed integration
Visual feedback with reference rectangle
