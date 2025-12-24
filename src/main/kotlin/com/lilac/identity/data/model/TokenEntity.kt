package com.lilac.identity.data.model

import com.lilac.identity.domain.enum.TokenType
import java.util.UUID

data class TokenEntity(
    val id: UUID,
    val userId: UUID,
    val tokenHashed: String,
    val tokenType: TokenType,
    val isUsed: Boolean,
    val usedAt: Long?,
    val issuedAt: Long,
    val expiresAt: Long
)