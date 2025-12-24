package com.lilac.identity.domain.repository

interface MailRepository {
    suspend fun sendEmailVerification(
        email: String,
        fullName: String,
        link: String,
        expiresInMin: Long
    ): Boolean

    suspend fun sendPasswordResetEmail(
        email: String,
        fullName: String,
        link: String,
        expiresInMin: Long
    ): Boolean

    suspend fun sendWelcomeEmail(
        email: String,
        fullName: String
    ): Boolean
}