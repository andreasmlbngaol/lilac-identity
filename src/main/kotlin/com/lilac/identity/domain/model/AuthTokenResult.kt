package com.lilac.identity.domain.model

import com.lilac.identity.domain.enum.AuthTokenType

data class AuthTokenResult(
    val token: String,
    val type: AuthTokenType,
    val issuedAt: Long,
    val expiresAt: Long
)
