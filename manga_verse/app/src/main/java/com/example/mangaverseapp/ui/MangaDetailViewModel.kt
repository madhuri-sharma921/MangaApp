package com.example.mangaverseapp.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mangaverseapp.data.repository.MangaRepository
import com.example.mangaverseapp.domain.model.MangaDetailState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MangaDetailViewModel @Inject constructor(
    private val mangaRepository: MangaRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val mangaId: String = checkNotNull(savedStateHandle["mangaId"])

    private val _mangaDetailState = MutableStateFlow<MangaDetailState>(MangaDetailState.Loading)
    val mangaDetailState: StateFlow<MangaDetailState> = _mangaDetailState

    init {
        loadMangaDetails(mangaId)
    }

    fun loadMangaDetails(mangaId: String) {
        viewModelScope.launch {
            mangaRepository.getMangaById(mangaId).collectLatest { manga ->
                _mangaDetailState.value = if (manga != null) {
                    MangaDetailState.Success(manga)
                } else {
                    MangaDetailState.Error("Manga not found")
                }
            }
        }
    }
}
