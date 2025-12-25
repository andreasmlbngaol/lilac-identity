package com.lilac.identity.config

import io.ktor.server.application.Application

data class AppConfig(
    val port: Int,
    val domain: String
)

fun Application.loadAppConfig(): AppConfig {
    val cfg = environment.config

    return AppConfig(
        port = cfg.propertyOrNull("ktor.deployment.port")?.getString()?.toInt()
            ?: throw IllegalStateException("ktor.deployment.port must be specified"),
        domain = cfg.propertyOrNull("ktor.domain")?.getString()
            ?: throw IllegalStateException("ktor.domain must be specified")
    )
}