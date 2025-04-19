//package com.example.mangaverseapp.data.repository
//
//import androidx.paging.Pager
//import androidx.paging.PagingConfig
//import androidx.paging.PagingData
//import androidx.paging.PagingSource
//import androidx.paging.PagingState
//import com.example.mangaverseapp.domain.model.Manga
//import com.example.mangaverseapp.domain.repository.MangaRepository
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.flow.map
//import kotlinx.coroutines.sync.Mutex
//import kotlinx.coroutines.sync.withLock
//import javax.inject.Inject
//import javax.inject.Singleton
//
//@Singleton
//class MangaRepositoryImpl @Inject constructor(
//    private val remoteDataSource: MangaRemoteDataSource
//) : MangaRepository {
//    // In-memory storage for mangas
//    private val _mangaList = MutableStateFlow<List<Manga>>(emptyList())
//
//    // Storage for favorite mangas
//    private val _favoriteMangas = MutableStateFlow<List<Manga>>(emptyList())
//
//    // Mutex for thread-safe operations
//    private val mutex = Mutex()
//
//    override fun getMangaStream(): Flow<PagingData<Manga>> {
//        return Pager(
//            config = PagingConfig(
//                pageSize = 20,
//                enablePlaceholders = false,
//                initialLoadSize = 20
//            ),
//            pagingSourceFactory = {
//                MangaPagingSource(remoteDataSource)
//            }
//        ).flow
//    }
//
//    // Custom PagingSource to handle remote data source pagination
//    private class MangaPagingSource(
//        private val remoteDataSource: MangaRemoteDataSource
//    ) : PagingSource<Int, Manga>() {
//        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Manga> {
//            return try {
//                val page = params.key ?: 1
//                val pageSize = params.loadSize
//
//                // Fetch mangas from remote data source
//                val response = remoteDataSource.getMangaList(page, pageSize)
//
//                LoadResult.Page(
//                    data = response.mangas,
//                    prevKey = if (page == 1) null else page - 1,
//                    nextKey = if (response.mangas.isEmpty()) null else page + 1
//                )
//            } catch (e: Exception) {
//                LoadResult.Error(e)
//            }
//        }
//
//        override fun getRefreshKey(state: PagingState<Int, Manga>): Int? {
//            return state.anchorPosition?.let { anchorPosition ->
//                state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
//                    ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
//            }
//        }
//    }
//
//    override fun getMangaById(id: String): Flow<Manga?> {
//        return _mangaList.map { mangaList ->
//            mangaList.find { it.id == id } ?:
//            remoteDataSource.getMangaById(id)
//        }
//    }
//
//    override suspend fun refreshMangas() {
//        mutex.withLock {
//            val fetchedMangas = remoteDataSource.getMangaList(1, 20).mangas
//            _mangaList.value = fetchedMangas
//        }
//    }
//
//    override suspend fun isCacheEmpty(): Boolean {
//        return _mangaList.value.isEmpty()
//    }
//
//    override suspend fun toggleFavorite(mangaId: String): Boolean {
//        return mutex.withLock {
//            val currentList = _mangaList.value.toMutableList()
//            val index = currentList.indexOfFirst { it.id == mangaId }
//
//            if (index == -1) return false
//
//            val manga = currentList[index]
//            val updatedManga = manga.copy(isFavorite = !manga.isFavorite)
//
//            currentList[index] = updatedManga
//            _mangaList.value = currentList
//
//            // Update favorites list
//            val currentFavorites = _favoriteMangas.value.toMutableList()
//            if (updatedManga.isFavorite) {
//                currentFavorites.add(updatedManga)
//            } else {
//                currentFavorites.removeAll { it.id == mangaId }
//            }
//            _favoriteMangas.value = currentFavorites
//
//            updatedManga.isFavorite
//        }
//    }
//
//    override suspend fun getFavoriteMangas(): Flow<List<Manga>> {
//        return _favoriteMangas.asStateFlow()
//    }
//}
//
//// Data class to represent paginated response
//data class MangaListResponse(
//    val mangas: List<Manga>,
//    val totalPages: Int
//)
//
//// Interface for remote data source
//interface MangaRemoteDataSource {
//    suspend fun getMangaList(page: Int, pageSize: Int): MangaListResponse
//
//    suspend fun getMangaById(id: String): Manga?
//}