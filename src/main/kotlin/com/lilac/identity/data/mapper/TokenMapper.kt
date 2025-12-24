package com.lilac.identity.data.mapper

import com.lilac.identity.data.model.TokenEntity
import com.lilac.identity.domain.model.Token

fun TokenEntity.toDomain() = Token(
    id = this.id.toString(),
    userId = this.userId.toString(),
    tokenHashed = this.tokenHashed,
    tokenType = this.tokenType,
    isUsed = this.isUsed,
    usedAt = this.usedAt,
    issuedAt = this.issuedAt,
    expiresAt = this.expiresAt,
)