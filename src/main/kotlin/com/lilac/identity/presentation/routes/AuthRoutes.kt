package com.lilac.identity.presentation.routes

import com.lilac.identity.domain.model.ValidationResult
import com.lilac.identity.domain.usecase.AuthUseCase
import com.lilac.identity.presentation.mapper.toDto
import com.lilac.identity.presentation.request.LoginRequest
import com.lilac.identity.presentation.request.RegisterRequest
import com.lilac.identity.presentation.response.TokenPairResponse
import com.lilac.identity.presentation.validator.RegisterValidator
import com.lilac.identity.util.respondError
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject

fun Route.authRoutes() {
    val authUseCase by inject<AuthUseCase>()

    route("/auth") {
        // Tested
        post("/register") {
            val unfilteredPayload = call.receive<RegisterRequest>()
            val payload = unfilteredPayload.copy(
                email = unfilteredPayload.email.lowercase().trim(),
                username = unfilteredPayload.username.lowercase().trim(),
                password = unfilteredPayload.password.trim(),
                firstName = unfilteredPayload.firstName.trim(),
                lastName = unfilteredPayload.lastName.trim()
            )
            val clientId = call.request.headers["X-Client-Id"]
                ?: return@post call.respondError(
                    HttpStatusCode.BadRequest,
                    "Missing X-Client-Id header"
                )
            val validationResult = RegisterValidator.validate(payload)

            if(validationResult is ValidationResult.Invalid)
                return@post call.respondError(
                    HttpStatusCode.BadRequest,
                    validationResult.errors.entries.joinToString("; ") {
                        "${it.key}: ${it.value}"
                    }
                )

            val tokenPair = authUseCase.register(
                email = payload.email,
                username = payload.username,
                password = payload.password,
                firstName = payload.firstName,
                lastName = payload.lastName,
                clientId = clientId
            )

            call.respond(
                HttpStatusCode.Created,
                TokenPairResponse(
                    data = tokenPair.toDto(),
                    message = "User created successfully"
                )
            )
        }

        // Tested
        post("/login") {
            val unfilteredPayload = call.receive<LoginRequest>()
            val payload = unfilteredPayload.copy(
                identifier = unfilteredPayload.identifier.lowercase().trim(),
                password = unfilteredPayload.password.trim()
            )
            val clientId = call.request.headers["X-Client-Id"]
                ?: return@post call.respondError(
                    HttpStatusCode.BadRequest,
                    "Missing X-Client-Id header"
                )

            if(payload.identifier.isBlank() || payload.password.isBlank()) {
                return@post call.respondError(
                    HttpStatusCode.BadRequest,
                    "Email or username and password are required"
                )
            }

            val tokenPair = authUseCase.login(
                emailOrUsername = payload.identifier,
                password = payload.password,
                clientId = clientId
            )
            call.respond(
                HttpStatusCode.OK,
                TokenPairResponse(
                    data = tokenPair.toDto(),
                    message = "Login successful"
                )
            )
        }

        verifyEmailRoutes(authUseCase)
    }
}