package com.lilac.identity.domain.usecase

import com.lilac.identity.domain.model.UserDetail
import com.lilac.identity.domain.model.UserNotFoundException
import com.lilac.identity.domain.repository.UserProfileRepository
import com.lilac.identity.domain.repository.UserRepository

class UserUseCase(
    private val userRepository: UserRepository,
    private val userProfileRepository: UserProfileRepository
) {
    suspend fun getUserDetail(
        userId: String
    ): UserDetail {
        val user = userRepository.findById(userId)
            ?: throw UserNotFoundException()

        val profile = userProfileRepository.findByUserId(userId)

        return UserDetail(
            id = user.id,
            email = user.email,
            username = user.username,
            firstName = user.firstName,
            lastName = user.lastName,
            isEmailVerified = user.isEmailVerified,
            createdAt = user.createdAt,
            updatedAt = user.updatedAt,
            profile = profile
        )
    }
}