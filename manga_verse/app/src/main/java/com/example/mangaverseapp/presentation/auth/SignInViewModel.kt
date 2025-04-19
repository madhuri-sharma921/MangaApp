package com.example.mangaverseapp.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.mangaverseapp.domain.model.User
import com.example.mangaverseapp.domain.usecase.GetCurrentUserUseCase
import com.example.mangaverseapp.domain.usecase.SignInUseCase

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase,
    getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    val currentUser = getCurrentUserUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    private val _signInState = MutableStateFlow<SignInState>(SignInState.Initial)
    val signInState: StateFlow<SignInState> = _signInState

    fun signIn(email: String, password: String) {
        _signInState.value = SignInState.Loading

        viewModelScope.launch {
            val result = signInUseCase(email, password)

            _signInState.value = result.fold(
                onSuccess = { SignInState.Success(it) },
                onFailure = { SignInState.Error(it.message ?: "Unknown error") }
            )
        }
    }

    fun resetState() {
        _signInState.value = SignInState.Initial
    }
}

sealed class SignInState {
    object Initial : SignInState()
    object Loading : SignInState()
    data class Success(val user: User) : SignInState()
    data class Error(val message: String) : SignInState()
}