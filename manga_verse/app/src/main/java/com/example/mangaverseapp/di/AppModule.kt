package com.example.mangaverseapp.di

import android.content.Context
import androidx.room.Room
import com.example.mangaverseapp.data.local.database.MangaDatabase
import com.example.mangaverseapp.data.remote.MangaApiService
import com.example.mangaverseapp.data.repository.util.NetworkConnectivityMonitor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNetworkConnectivityMonitor(
        @ApplicationContext context: Context
    ): NetworkConnectivityMonitor {
        return NetworkConnectivityMonitor(context)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideMangaApiService(okHttpClient: OkHttpClient): MangaApiService {
        return Retrofit.Builder()
            .baseUrl("https://mangaverse-api.p.rapidapi.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MangaApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideMangaDatabase(@ApplicationContext context: Context): MangaDatabase {
        return Room.databaseBuilder(
            context,
            MangaDatabase::class.java,
            "manga_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideMangaDao(database: MangaDatabase) = database.mangaDao()

    @Provides
    @Singleton
    fun provideRemoteKeysDao(database: MangaDatabase) = database.remoteKeysDao()
}