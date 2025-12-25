package com.lilac.identity.domain.model

data class RefreshTokenClaims(
    val userId: String,
    val issuedAt: Long,
    val expiresAt: Long
)
