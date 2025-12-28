package com.lilac.identity.config

import com.auth0.jwt.JWT
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import org.koin.ktor.ext.getKoin

fun Application.configureSecurity() {
    val jwt = getKoin().get<AuthConfig>()

    install(Authentication) {
        jwt(AppConstant.JWT_NAME) {
            realm = jwt.realm
            verifier(
                JWT
                    .require(jwt.algorithm)
                    .withIssuer(jwt.issuer)
                    .build()
            )

            validate { credential ->
                val payload = credential.payload

                payload.subject?.takeIf { it.isNotBlank() } ?: return@validate null
                val type = payload.getClaim("type").asString()

                if (type != "Access") return@validate null

                JWTPrincipal(payload)
            }
        }
    }
}