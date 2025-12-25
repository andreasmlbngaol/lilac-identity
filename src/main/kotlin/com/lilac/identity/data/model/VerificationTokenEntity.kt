package com.lilac.identity.data.model

import com.lilac.identity.domain.enum.VerificationTokenType
import java.util.UUID

data class VerificationTokenEntity(
    val id: UUID,
    val userId: UUID,
    val tokenHashed: String,
    val verificationTokenType: VerificationTokenType,
    val isUsed: Boolean,
    val usedAt: Long?,
    val issuedAt: Long,
    val expiresAt: Long
)