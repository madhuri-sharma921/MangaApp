package com.example.mangaverseapp.data.repository.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import retrofit2.HttpException
import com.example.mangaverseapp.data.local.database.MangaDatabase
import com.example.mangaverseapp.data.local.entity.MangaRemoteKeys
import com.example.mangaverseapp.data.remote.MangaApiService
import com.example.mangaverseapp.domain.model.MangaModel
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class MangaRemoteMediator(
    private val mangaApiService: MangaApiService,
    private val mangaDatabase: MangaDatabase
) : RemoteMediator<Int, MangaModel>() {

    private val mangaDao = mangaDatabase.mangaDao()
    private val remoteKeysDao = mangaDatabase.remoteKeysDao()

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MangaModel>
    ): MediatorResult {
        try {
            val page = when (loadType) {
                LoadType.REFRESH -> {
                    val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                    remoteKeys?.nextKey?.minus(1) ?: STARTING_PAGE_INDEX
                }
                LoadType.PREPEND -> {
                    val remoteKeys = getRemoteKeyForFirstItem(state)
                    val prevKey = remoteKeys?.prevKey
                        ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                    prevKey
                }
                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    val nextKey = remoteKeys?.nextKey
                        ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                    nextKey
                }
            }

            val response = mangaApiService.fetchMangas(page = page, limit = state.config.pageSize)
            val mangas = response.data
            val endOfPaginationReached = mangas.isEmpty() || response.currentPage >= response.totalPages

            mangaDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    remoteKeysDao.clearRemoteKeys()
                    mangaDao.clearAll()
                }

                val prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = mangas.map {
                    MangaRemoteKeys(mangaId = it.id, prevKey = prevKey, nextKey = nextKey)
                }

                remoteKeysDao.insertAll(keys)
                mangaDao.insertAll(mangas)
            }

            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, MangaModel>): MangaRemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { manga ->
                remoteKeysDao.getRemoteKeysByMangaId(manga.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, MangaModel>): MangaRemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { manga ->
                remoteKeysDao.getRemoteKeysByMangaId(manga.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, MangaModel>): MangaRemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { mangaId ->
                remoteKeysDao.getRemoteKeysByMangaId(mangaId)
            }
        }
    }

    companion object {
        private const val STARTING_PAGE_INDEX = 1
    }
}
