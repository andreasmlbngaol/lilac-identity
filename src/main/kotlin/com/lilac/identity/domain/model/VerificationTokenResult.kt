package com.lilac.identity.domain.model

import com.lilac.identity.domain.enum.VerificationTokenType

data class VerificationTokenResult(
    val token: String,
    val type: VerificationTokenType,
    val issuedAt: Long,
    val expiresAt: Long
)
