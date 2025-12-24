package com.lilac.identity.domain.repository

import com.lilac.identity.domain.model.Token
import com.lilac.identity.domain.enum.TokenType

interface TokenRepository {
    suspend fun create(
        userId: String,
        tokenHash: String,
        tokenType: TokenType,
        issuedAt: Long,
        expiresAt: Long
    ): String

    suspend fun findActiveByUserIdAndType(
        userId: String,
        tokenType: TokenType
    ): Token?

    suspend fun findByTokenHash(tokenHah: String): Token?

    suspend fun markAsUsed(
        tokenId: String
    ): Boolean

    suspend fun deleteByUserIdAndType(
        userId: String,
        tokenType: TokenType
    ): Boolean

    suspend fun deleteExpiredTokens(): Int
}