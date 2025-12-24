package com.lilac.identity.presentation.routes

import com.lilac.identity.domain.model.ValidationResult
import com.lilac.identity.domain.usecase.AuthUseCase
import com.lilac.identity.domain.validator.RegisterValidator
import com.lilac.identity.presentation.mapper.toDto
import com.lilac.identity.presentation.request.RegisterRequest
import com.lilac.identity.presentation.response.TokenPairResponse
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
    val registerValidator by inject<RegisterValidator>()

    route("/auth") {
        post("/register") {
            val unfilteredPayload = call.receive<RegisterRequest>()
            val payload = unfilteredPayload.copy(
                email = unfilteredPayload.email.lowercase().trim(),
                username = unfilteredPayload.username.lowercase().trim(),
                password = unfilteredPayload.password.trim(),
                firstName = unfilteredPayload.firstName.trim(),
                lastName = unfilteredPayload.lastName.trim()
            )

            val validationResult = registerValidator.validate(
                email = payload.email,
                username = payload.username,
                password = payload.password,
                firstName = payload.firstName,
                lastName = payload.lastName
            )
            if(validationResult is ValidationResult.Invalid)
                return@post call.respondError(
                    HttpStatusCode.BadRequest,
                    validationResult.errors.entries.joinToString("; ") {
                        "${it.key}: ${it.value}"
                    }
                )

            val tokenPair = authUseCase.registerUser(
                email = payload.email,
                username = payload.username,
                password = payload.password,
                firstName = payload.firstName,
                lastName = payload.lastName
            )

            call.respond(
                HttpStatusCode.Created,
                TokenPairResponse(
                    data = tokenPair.toDto(),
                    message = "User created successfully"
                )
            )
        }

        verifyEmailRoutes(authUseCase)
    }
}