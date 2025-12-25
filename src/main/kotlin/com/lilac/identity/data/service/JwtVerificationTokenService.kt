package com.lilac.identity.data.service

import com.auth0.jwt.JWT
import com.lilac.identity.config.VerificationConfig
import com.lilac.identity.domain.enum.VerificationTokenType
import com.lilac.identity.domain.model.VerificationTokenClaims
import com.lilac.identity.domain.model.VerificationTokenResult
import com.lilac.identity.domain.service.VerificationTokenDecoder
import com.lilac.identity.domain.service.VerificationTokenGenerator
import java.util.Date

class JwtVerificationTokenService(
    val config: VerificationConfig
): VerificationTokenGenerator, VerificationTokenDecoder {
    override fun generateEmailVerificationToken(userId: String): VerificationTokenResult {
        val issuedAt = System.currentTimeMillis()
        val expiresAt = issuedAt + config.emailVerificationTokenExpInMillis

        val token = JWT.create()
            .withIssuer(config.issuer)
            .withAudience(config.audience)
            .withSubject(userId)
            .withClaim("type", VerificationTokenType.EmailVerification.name)
            .withIssuedAt(Date(issuedAt))
            .withExpiresAt(Date(expiresAt))
            .sign(config.algorithm)

        return VerificationTokenResult(
            token = token,
            type = VerificationTokenType.EmailVerification,
            issuedAt = issuedAt,
            expiresAt = expiresAt
        )
    }

    override fun generateResetPasswordToken(userId: String): VerificationTokenResult {
        val issuedAt = System.currentTimeMillis()
        val expiresAt = issuedAt + config.resetPasswordTokenExpInMillis

        val token = JWT.create()
            .withIssuer(config.issuer)
            .withAudience(config.audience)
            .withSubject(userId)
            .withClaim("type", VerificationTokenType.ResetPassword.name)
            .withIssuedAt(Date(issuedAt))
            .withExpiresAt(Date(expiresAt))
            .sign(config.algorithm)

        return VerificationTokenResult(
            token = token,
            type = VerificationTokenType.ResetPassword,
            issuedAt = issuedAt,
            expiresAt = expiresAt
        )
    }

    private val verifier = JWT.require(config.algorithm)
        .withIssuer(config.issuer)
        .withAudience(config.audience)
        .build()

    override fun decodeEmailVerificationToken(token: String): VerificationTokenClaims? = try {
        val decoded = verifier.verify(token)
        if(decoded.getClaim("type").asString() == VerificationTokenType.EmailVerification.name) {
            VerificationTokenClaims(
                userId = decoded.subject,
                issuedAt = decoded.issuedAt.time,
                expiresAt = decoded.expiresAt.time
            )
        } else null
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

    override fun decodeResetPasswordToken(token: String): VerificationTokenClaims? = try {
        val decoded = verifier.verify(token)
        if(decoded.getClaim("type").asString() == VerificationTokenType.ResetPassword.name) {
            VerificationTokenClaims(
                userId = decoded.subject,
                issuedAt = decoded.issuedAt.time,
                expiresAt = decoded.expiresAt.time
            )
        } else null
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}