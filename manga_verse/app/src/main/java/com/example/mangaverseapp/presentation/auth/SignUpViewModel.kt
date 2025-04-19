package com.example.mangaverseapp.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mangaverseapp.domain.model.User
import com.example.mangaverseapp.domain.usecase.SignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase
) : ViewModel() {

    private val _signUpState = MutableStateFlow<SignUpUiState>(SignUpUiState.Initial)
    val signUpState: StateFlow<SignUpUiState> = _signUpState.asStateFlow()

    fun signUp(email: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            // Set loading state
            _signUpState.value = SignUpUiState.Loading

            // Perform signup
            val result = signUpUseCase(email, password, confirmPassword)

            // Handle the Result
            _signUpState.value = when {
                result.isSuccess -> SignUpUiState.Success(result.getOrNull()!!)
                result.isFailure -> SignUpUiState.Error(
                    result.exceptionOrNull()?.message ?: "Unknown error occurred"
                )
                else -> SignUpUiState.Error("Unexpected signup result")
            }
        }
    }

    fun resetState() {
        _signUpState.value = SignUpUiState.Initial
    }

    // Sealed class for UI state
    sealed class SignUpUiState {
        object Initial : SignUpUiState()
        object Loading : SignUpUiState()
        data class Success(val user: User) : SignUpUiState()
        data class Error(val message: String) : SignUpUiState()
    }
}