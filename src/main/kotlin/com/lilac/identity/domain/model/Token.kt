package com.lilac.identity.domain.model

import com.lilac.identity.domain.enum.TokenType

data class Token(
    val id: String,
    val userId: String,
    val tokenHashed: String,
    val tokenType: TokenType,
    val isUsed: Boolean = false,
    val usedAt: Long? = null,
    val issuedAt: Long,
    val expiresAt: Long
)