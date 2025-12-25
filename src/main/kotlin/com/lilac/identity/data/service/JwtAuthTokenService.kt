package com.lilac.identity.data.service

import com.auth0.jwt.JWT
import com.lilac.identity.config.AuthConfig
import com.lilac.identity.domain.enum.AuthTokenType
import com.lilac.identity.domain.model.AccessTokenClaims
import com.lilac.identity.domain.model.AuthTokenResult
import com.lilac.identity.domain.model.RefreshTokenClaims
import com.lilac.identity.domain.service.AuthTokenDecoder
import com.lilac.identity.domain.service.AuthTokenGenerator
import java.util.Date

class JwtAuthTokenService(
    private val config: AuthConfig
): AuthTokenGenerator, AuthTokenDecoder {
    override fun generateAccessToken(
        userId: String,
        username: String,
        firstName: String,
        lastName: String,
        isEmailVerified: Boolean
    ): AuthTokenResult {
        val issuedAt = System.currentTimeMillis()
        val expiresAt = issuedAt + config.accessTokenExpInMillis

        val token = JWT.create()
            .withIssuer(config.issuer)
            .withAudience(config.audience)
            .withSubject(userId)
            .withClaim("username", username)
            .withClaim("first_name", firstName)
            .withClaim("last_name", lastName)
            .withClaim("email_verified", isEmailVerified)
            .withClaim("type", AuthTokenType.Access.name)
            .withIssuedAt(Date(issuedAt))
            .withExpiresAt(Date(expiresAt))
            .sign(config.algorithm)

        return AuthTokenResult(
            token = token,
            type = AuthTokenType.Access,
            issuedAt = issuedAt,
            expiresAt = expiresAt
        )
    }

    override fun generateRefreshToken(userId: String): AuthTokenResult {
        val issuedAt = System.currentTimeMillis()
        val expiresAt = issuedAt + config.refreshTokenExpInMillis

        val token = JWT.create()
            .withIssuer(config.issuer)
            .withAudience(config.audience)
            .withSubject(userId)
            .withClaim("type", AuthTokenType.Refresh.name)
            .withIssuedAt(Date(issuedAt))
            .withExpiresAt(Date(expiresAt))
            .sign(config.algorithm)

        return AuthTokenResult(
            token = token,
            type = AuthTokenType.Refresh,
            issuedAt = issuedAt,
            expiresAt = expiresAt
        )
    }

    private val verifier = JWT.require(config.algorithm)
        .withAudience(config.audience)
        .withIssuer(config.issuer)
        .build()

    override fun decodeAccessToken(token: String): AccessTokenClaims? = try {
        val decoded = verifier.verify(token)
        if(decoded.getClaim("type").asString() == AuthTokenType.Access.name) {
            AccessTokenClaims(
                userId = decoded.subject,
                username = decoded.getClaim("username").asString(),
                firstName = decoded.getClaim("first_name").asString(),
                lastName = decoded.getClaim("last_name").asString(),
                isEmailVerified = decoded.getClaim("email_verified").asBoolean(),
                issuedAt = decoded.issuedAt.time,
                expiresAt = decoded.expiresAt.time
            )
        } else null
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

    override fun decodeRefreshToken(token: String): RefreshTokenClaims? = try {
        val decoded = verifier.verify(token)
        if(decoded.getClaim("type").asString() == AuthTokenType.Refresh.name) {
            RefreshTokenClaims(
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