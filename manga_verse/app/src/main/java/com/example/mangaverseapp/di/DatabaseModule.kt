package com.example.mangaverseapp.di



import android.content.Context
import androidx.room.Room

import com.example.mangaverseapp.data.local.AppDatabase

import com.example.mangaverseapp.data.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideUserDao(appDatabase: AppDatabase) = appDatabase.userDao()


 //   fun provideMangaDao(appDatabase: AppDatabase) = appDatabase.mangaDao()


   // fun provideRemoteKeysDao(appDatabase: AppDatabase) = appDatabase.remoteKeysDao()
}
