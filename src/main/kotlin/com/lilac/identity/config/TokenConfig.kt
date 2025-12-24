package com.lilac.identity.config

import io.ktor.server.application.Application

data class TokenConfig(
    val secret: String
)

fun Application.loadTokenConfig(): TokenConfig {
    val cfg = environment.config

    return TokenConfig(
        secret = cfg.propertyOrNull("token.secret")?.getString() ?: error("Token Secret must be specified")
    )
}
