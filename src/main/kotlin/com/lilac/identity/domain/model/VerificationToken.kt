package com.lilac.identity.domain.model

import com.lilac.identity.domain.enum.VerificationTokenType

data class VerificationToken(
    val id: String,
    val userId: String,
    val tokenHashed: String,
    val verificationTokenType: VerificationTokenType,
    val isUsed: Boolean = false,
    val usedAt: Long? = null,
    val issuedAt: Long,
    val expiresAt: Long
)