package com.lilac.identity.data.mapper

import com.lilac.identity.data.model.VerificationTokenEntity
import com.lilac.identity.domain.model.VerificationToken

fun VerificationTokenEntity.toDomain() = VerificationToken(
    id = this.id.toString(),
    userId = this.userId.toString(),
    tokenHashed = this.tokenHashed,
    verificationTokenType = this.verificationTokenType,
    isUsed = this.isUsed,
    usedAt = this.usedAt,
    issuedAt = this.issuedAt,
    expiresAt = this.expiresAt,
)