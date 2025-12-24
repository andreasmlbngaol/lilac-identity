package com.lilac.identity.domain.repository

import com.lilac.identity.domain.model.UserProfile

interface UserProfileRepository {
    suspend fun findByUserId(userId: String): UserProfile?
    suspend fun create(
        userId: String,
        bio: String?,
        profilePictureUrl: String?,
        coverPictureUrl: String?
    ): String
    suspend fun update(profile: UserProfile): Boolean
}