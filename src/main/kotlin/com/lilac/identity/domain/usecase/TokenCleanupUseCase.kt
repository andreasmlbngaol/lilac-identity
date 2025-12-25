package com.lilac.identity.domain.usecase

import com.lilac.identity.domain.repository.VerificationTokenRepository

class TokenCleanupUseCase(
    private val verificationTokenRepository: VerificationTokenRepository
) {
    suspend fun cleanupExpiredToken(): Int {
        return verificationTokenRepository.deleteExpiredTokens()
    }
}