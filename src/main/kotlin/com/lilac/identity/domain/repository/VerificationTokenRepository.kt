package com.lilac.identity.domain.repository

import com.lilac.identity.domain.model.VerificationToken
import com.lilac.identity.domain.enum.VerificationTokenType

interface VerificationTokenRepository {
    suspend fun create(
        userId: String,
        tokenHash: String,
        verificationTokenType: VerificationTokenType,
        issuedAt: Long,
        expiresAt: Long
    ): String

    suspend fun findActiveByUserIdAndType(
        userId: String,
        verificationTokenType: VerificationTokenType
    ): VerificationToken?

    suspend fun findByTokenHash(tokenHah: String): VerificationToken?

    suspend fun markAsUsed(
        tokenId: String
    ): Boolean

    suspend fun deleteByUserIdAndType(
        userId: String,
        verificationTokenType: VerificationTokenType
    ): Boolean

    suspend fun deleteExpiredTokens(): Int
}