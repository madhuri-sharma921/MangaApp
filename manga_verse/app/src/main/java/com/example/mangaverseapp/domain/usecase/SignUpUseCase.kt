package com.example.mangaverseapp.domain.usecase

import com.example.mangaverseapp.domain.model.User
import com.example.mangaverseapp.domain.repository.UserRepository
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(email: String, password: String, confirmPassword: String): Result<User> {
        // Validate inputs
        if (email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            return Result.failure(Exception("Fields cannot be empty"))
        }

        if (!email.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"))) {
            return Result.failure(Exception("Invalid email format"))
        }

        if (password.length < 6) {
            return Result.failure(Exception("Password must be at least 6 characters"))
        }

        if (password != confirmPassword) {
            return Result.failure(Exception("Passwords do not match"))
        }

        // Use createUser directly for explicit sign-up
        return userRepository.createUser(email, password)
    }
}