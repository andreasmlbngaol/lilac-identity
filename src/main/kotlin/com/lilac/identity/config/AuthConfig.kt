package com.lilac.identity.config

import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.Application

data class AuthConfig(
    val issuer: String,
    val audience: String,
    val realm: String,
    private val secret: String,
    private val accessTokenExpInMins: Long,
    private val refreshTokenExpInDays: Long,
) {
    val accessTokenExpInMillis = accessTokenExpInMins * 60 * 1000
    val refreshTokenExpInMillis = refreshTokenExpInDays * 24 * 60 * 60 * 1000
    val algorithm: Algorithm = Algorithm.HMAC512(secret)
}

fun Application.loadAuthConfig(): AuthConfig {
    val cfg = environment.config

    return AuthConfig(
        realm = cfg.propertyOrNull("auth.realm")?.getString() ?: error("JWT Realm must be specified"),
        secret = cfg.propertyOrNull("auth.secret")?.getString() ?: error("JWT Secret must be specified"),
        audience = cfg.propertyOrNull("auth.audience")?.getString() ?: error("JWT Audience must be specified"),
        issuer = cfg.propertyOrNull("auth.issuer")?.getString() ?: error("JWT Issuer must be specified"),
        accessTokenExpInMins = cfg.propertyOrNull("auth.accessTokenExpInMins")?.getString()?.toLong() ?: 10,
        refreshTokenExpInDays = cfg.propertyOrNull("auth.refreshTokenExpInDays")?.getString()?.toLong() ?: 30,
    )
}