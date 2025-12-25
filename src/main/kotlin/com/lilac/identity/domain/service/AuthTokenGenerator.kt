package com.lilac.identity.domain.service

import com.lilac.identity.domain.model.AuthTokenResult

interface AuthTokenGenerator {
    fun generateAccessToken(
        userId: String,
        username: String,
        firstName: String,
        lastName: String,
        isEmailVerified: Boolean
    ): AuthTokenResult

    fun generateRefreshToken(
        userId: String
    ): AuthTokenResult
}