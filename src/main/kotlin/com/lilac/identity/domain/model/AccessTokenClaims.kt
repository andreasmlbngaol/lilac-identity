package com.lilac.identity.domain.model

data class AccessTokenClaims(
    val userId: String,
    val username: String,
    val firstName: String,
    val lastName: String,
    val isEmailVerified: Boolean,
    val issuedAt: Long,
    val expiresAt: Long
)
