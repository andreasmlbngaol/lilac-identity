package com.lilac.identity.config

import io.ktor.server.application.Application

data class VerificationHashConfig(
    val secret: String
)

fun Application.loadVerificationHashConfig(): VerificationHashConfig {
    val cfg = environment.config

    return VerificationHashConfig(
        secret = cfg.propertyOrNull("verification.hashSecret")?.getString() ?: error("Verification Hash Secret must be specified")
    )
}
