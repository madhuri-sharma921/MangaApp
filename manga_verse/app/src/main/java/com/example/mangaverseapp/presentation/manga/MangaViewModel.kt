//package com.example.mangaverseapp.presentation.manga
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import androidx.paging.PagingData
//import androidx.paging.cachedIn
//import com.example.mangaverseapp.data.local.entity.MangaEntity
//import com.example.mangaverseapp.domain.model.Manga
//import com.example.mangaverseapp.domain.repository.MangaRepository
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.flow.*
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//@HiltViewModel
//class MangaViewModel @Inject constructor(
//    private val repository: MangaRepository
//) : ViewModel() {
//
//    private val _selectedManga = MutableStateFlow<MangaEntity?>(null)
//    val selectedManga = _selectedManga.asStateFlow()
//
//    private val _mangaDetailState = MutableStateFlow<MangaDetailState>(MangaDetailState.Loading)
//    val mangaDetailState = _mangaDetailState.asStateFlow()
//
////    val mangaListFlow: Flow<PagingData<MangaEntity>> = repository
////        .getMangaStream()
////        .cachedIn(viewModelScope)
//
//    fun selectManga(id: String) {
//        viewModelScope.launch {
//    //        _selectedManga.value = repository.getMangaById(id)
//        }
//    }
//
//    fun loadMangaDetails(id: String) {
//        viewModelScope.launch {
//            _mangaDetailState.value = MangaDetailState.Loading
//            try {
//                val mangaEntity = repository.getMangaById(id)
//            //    val mangaDetails = mangaEntity?.toManga() // Convert MangaEntity to Manga
//              //  _mangaDetailState.value = mangaDetails?.let { MangaDetailState.Success(it) }
//                    ?: MangaDetailState.Error("Manga not found")
//            } catch (e: Exception) {
//                _mangaDetailState.value = MangaDetailState.Error(e.message ?: "Unknown error occurred")
//            }
//        }
//    }
//}
//
//sealed class MangaDetailState {
//    object Loading : MangaDetailState()
//    data class Success(val manga: Manga) : MangaDetailState()
//    data class Error(val message: String) : MangaDetailState()
//}