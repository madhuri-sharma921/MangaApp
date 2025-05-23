package com.example.mangaverseapp.di


import com.example.mangaverseapp.domain.repository.UserRepository
import com.example.mangaverseapp.repository.UserRepositoryImpl

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton


    abstract fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository
}