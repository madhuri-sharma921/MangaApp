package com.example.mangaverseapp.domain.usecase



import com.example.mangaverseapp.domain.model.User
import com.example.mangaverseapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<User?> {
        return userRepository.getCurrentUser()
    }
}