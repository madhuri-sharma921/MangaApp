package com.example.mangaverseapp.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.mangaverseapp.data.local.dao.MangaDao
import com.example.mangaverseapp.data.local.database.MangaDatabase
import com.example.mangaverseapp.data.remote.MangaApiService
import com.example.mangaverseapp.data.repository.paging.MangaRemoteMediator
import com.example.mangaverseapp.domain.model.MangaModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MangaRepository @Inject constructor(
    private val mangaApiService: MangaApiService,
    private val mangaDatabase: MangaDatabase,
    private val mangaDao: MangaDao
) {
    @OptIn(ExperimentalPagingApi::class)
    fun getMangaStream(): Flow<PagingData<MangaModel>> {
        val pagingSourceFactory = { mangaDao.getMangasPagingSource() }

        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            remoteMediator = MangaRemoteMediator(
                mangaApiService,
                mangaDatabase
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    fun getMangaById(mangaId: String): Flow<MangaModel?> {
        return mangaDao.getMangaById(mangaId)
    }

    suspend fun hasCachedData(): Boolean {
        return mangaDao.getMangaCount() > 0
    }

    companion object {
        const val NETWORK_PAGE_SIZE = 20
    }
}