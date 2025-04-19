package com.example.mangaverseapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.mangaverseapp.data.repository.MangaRepository
import com.example.mangaverseapp.data.repository.util.NetworkConnectivityMonitor
import com.example.mangaverseapp.domain.model.MangaModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MangaListViewModel @Inject constructor(
    private val mangaRepository: MangaRepository,
    private val networkConnectivityMonitor: NetworkConnectivityMonitor
) : ViewModel() {

    private val _isNetworkAvailable = MutableStateFlow(false)
    val isNetworkAvailable: StateFlow<Boolean> = _isNetworkAvailable.asStateFlow()

    private val _hasCachedData = MutableStateFlow(false)
    val hasCachedData: StateFlow<Boolean> = _hasCachedData.asStateFlow()

    val mangaListFlow: Flow<PagingData<MangaModel>> = mangaRepository.getMangaStream()
        .cachedIn(viewModelScope)

    init {
        checkNetworkStatus()
        checkCachedData()
    }

    private fun checkNetworkStatus() {
        viewModelScope.launch {
            networkConnectivityMonitor.observeNetworkStatus().collectLatest { isAvailable ->
                _isNetworkAvailable.value = isAvailable
            }
        }
    }

    private fun checkCachedData() {
        viewModelScope.launch {
            _hasCachedData.value = mangaRepository.hasCachedData()
        }
    }

    fun isOfflineWithCache(): Boolean {
        return !_isNetworkAvailable.value && _hasCachedData.value
    }
}