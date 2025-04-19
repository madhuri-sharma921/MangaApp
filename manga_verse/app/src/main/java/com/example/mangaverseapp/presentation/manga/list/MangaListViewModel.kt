//package com.example.mangaverseapp.presentation.manga.list
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import androidx.paging.PagingData
//import androidx.paging.cachedIn
//import com.example.mangaverseapp.domain.model.Manga
//import com.example.mangaverseapp.domain.usecase.GetMangaListUseCase
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//@HiltViewModel
//class MangaListViewModel @Inject constructor(
//    private val getMangaListUseCase: GetMangaListUseCase
//) : ViewModel() {
//
//    val mangaListFlow: Flow<PagingData<Manga>> = getMangaListUseCase()
//        .cachedIn(viewModelScope)
//
//    private val _isRefreshing = MutableStateFlow(false)
//    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()
//
//    private val _selectedMangaId = MutableStateFlow<String?>(null)
//    val selectedMangaId: StateFlow<String?> = _selectedMangaId.asStateFlow()
//
//    init {
//        viewModelScope.launch {
//            if (getMangaListUseCase.isCacheEmpty()) {
//                refreshMangas()
//            }
//        }
//    }
//
//    fun refreshMangas() {
//        viewModelScope.launch {
//            _isRefreshing.value = true
//            try {
//                getMangaListUseCase.refreshMangas()
//            } catch (e: Exception) {
//                // Handle error case
//                e.printStackTrace()
//            } finally {
//                _isRefreshing.value = false
//            }
//        }
//    }
//
//    fun selectManga(mangaId: String) {
//        _selectedMangaId.value = mangaId
//    }
//}