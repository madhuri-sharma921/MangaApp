package com.example.mangaverseapp.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import com.example.mangaverseapp.domain.repository.UserRepository

class GetAuthStatusUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<Boolean> {

        return userRepository.getCurrentUser().map { user -> user != null }
    }
}