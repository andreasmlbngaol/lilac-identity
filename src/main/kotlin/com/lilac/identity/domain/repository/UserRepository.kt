package com.lilac.identity.domain.repository

import com.lilac.identity.domain.model.User

interface UserRepository {
    suspend fun findById(id: String): User?
    suspend fun findByEmail(email: String): User?
    suspend fun findByEmailOrUsername(emailOrUsername: String): User?
    suspend fun existsById(id: String): Boolean
    suspend fun existsByEmail(email: String): Boolean
    suspend fun existsByUsername(username: String): Boolean
    suspend fun create(
        email: String,
        username: String,
        passwordHash: String,
        firstName: String,
        lastName: String,
        isEmailVerified: Boolean = false
    ): String

    suspend fun deleteById(
        id: String
    ): Boolean
    suspend fun updatePassword(id: String, newHash: String): Boolean
    suspend fun markEmailVerified(id: String): Boolean
}