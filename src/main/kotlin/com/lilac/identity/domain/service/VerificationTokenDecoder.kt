package com.lilac.identity.domain.service

import com.lilac.identity.domain.model.VerificationTokenClaims

interface VerificationTokenDecoder {
    fun decodeEmailVerificationToken(token: String): VerificationTokenClaims?
    fun decodeResetPasswordToken(token: String): VerificationTokenClaims?
}