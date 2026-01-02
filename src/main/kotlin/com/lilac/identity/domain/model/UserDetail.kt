package com.lilac.identity.domain.model

data class UserDetail(
    val id: String,
    val email: String,
    val username: String,
    val firstName: String,
    val lastName: String,
    val isEmailVerified: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
    val profile: UserProfile?
)