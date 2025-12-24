package com.lilac.identity.domain.usecase

import com.lilac.identity.domain.repository.TokenRepository

class TokenCleanupUseCase(
    private val tokenRepository: TokenRepository
) {
    suspend fun cleanupExpiredToken(): Int {
        return tokenRepository.deleteExpiredTokens()
    }
}