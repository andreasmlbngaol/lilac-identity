package com.lilac.identity.domain.service

import com.lilac.identity.domain.model.VerificationTokenResult

interface VerificationTokenGenerator {
    fun generateEmailVerificationToken(userId: String): VerificationTokenResult
    fun generateResetPasswordToken(userId: String): VerificationTokenResult
}