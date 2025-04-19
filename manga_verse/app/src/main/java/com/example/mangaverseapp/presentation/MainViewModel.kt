//package com.example.mangaverseapp.presentation
//
//import androidx.lifecycle.ViewModel
//
//
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.flow.Flow
//import javax.inject.Inject
//
//@HiltViewModel
//class MainViewModel @Inject constructor(
//    private val getAuthStateUseCase: GetAuthStateUseCase
//) : ViewModel() {
//
//    val isUserLoggedIn: Flow<Boolean> = getAuthStateUseCase()
//}