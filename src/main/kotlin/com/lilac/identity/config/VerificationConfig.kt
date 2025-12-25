package com.lilac.identity.config

import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.Application

data class VerificationConfig(
    val issuer: String,
    val audience: String,
    val realm: String,
    private val secret: String,
    private val emailVerificationTokenExpInMins: Long,
    private val resetPasswordTokenExpInMins: Long
) {
    val emailVerificationTokenExpInMillis = emailVerificationTokenExpInMins * 60 * 1000
    val resetPasswordTokenExpInMillis = resetPasswordTokenExpInMins * 60 * 1000
    val algorithm: Algorithm = Algorithm.HMAC512(secret)
}

fun Application.loadVerificationConfig(): VerificationConfig {
    val cfg = environment.config

    return VerificationConfig(
        issuer = cfg.property("verification.issuer").getString(),
        audience = cfg.property("verification.audience").getString(),
        realm = cfg.property("verification.realm").getString(),
        secret = cfg.property("verification.secret").getString(),
        emailVerificationTokenExpInMins = cfg.property("verification.emailVerificationTokenExpInMins").getString().toLong(),
        resetPasswordTokenExpInMins = cfg.property("verification.resetPasswordTokenExpInMins").getString().toLong()
    )
}