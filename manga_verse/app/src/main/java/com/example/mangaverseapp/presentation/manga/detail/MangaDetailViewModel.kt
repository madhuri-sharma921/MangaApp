//package com.example.mangaverseapp.presentation.manga.detail
//
//
//import androidx.lifecycle.SavedStateHandle
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//
//import com.example.mangaverseapp.domain.model.Manga
//import com.example.mangaverseapp.domain.usecase.GetMangaDetailsUseCase
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.collectLatest
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//@HiltViewModel
//class MangaDetailsViewModel @Inject constructor(
//    private val getMangaDetailsUseCase: GetMangaDetailsUseCase,
//    savedStateHandle: SavedStateHandle
//) : ViewModel() {
//
//    private val _mangaDetailsState = MutableStateFlow<MangaDetailsState>(MangaDetailsState.Loading)
//    val mangaDetailsState: StateFlow<MangaDetailsState> = _mangaDetailsState
//
//    init {
//        savedStateHandle.get<String>("mangaId")?.let { mangaId ->
//            loadMangaDetails(mangaId)
//        } ?: run {
//            _mangaDetailsState.value = MangaDetailsState.Error("Invalid manga ID")
//        }
//    }
//
//    private fun loadMangaDetails(mangaId: String) {
//        viewModelScope.launch {
//            getMangaDetailsUseCase(mangaId).collectLatest { manga ->
//                _mangaDetailsState.value = if (manga != null) {
//                    MangaDetailsState.Success(manga)
//                } else {
//                    MangaDetailsState.Error("Manga not found")
//                }
//            }
//        }
//    }
//}
//
//sealed class MangaDetailsState {
//    object Loading : MangaDetailsState()
//    data class Success(val manga: Manga) : MangaDetailsState()
//    data class Error(val message: String) : MangaDetailsState()
//}