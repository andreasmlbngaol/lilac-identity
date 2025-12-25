package com.lilac.identity.domain.model

data class VerificationTokenClaims(
    val userId: String,
    val issuedAt: Long,
    val expiresAt: Long
)
