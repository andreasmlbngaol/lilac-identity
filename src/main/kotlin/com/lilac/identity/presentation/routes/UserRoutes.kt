package com.lilac.identity.presentation.routes

import com.lilac.identity.config.AppConstant.JWT_NAME
import com.lilac.identity.domain.model.InvalidIdentifierException
import com.lilac.identity.domain.usecase.UserUseCase
import com.lilac.identity.presentation.mapper.toDto
import com.lilac.identity.presentation.mapper.toPublicDto
import com.lilac.identity.presentation.response.UserDetailResponse
import com.lilac.identity.presentation.response.UserPublicDetailResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject

fun Route.userRoutes() {
    val userUseCase by inject<UserUseCase>()

    route("/users") {
        authenticate(JWT_NAME) {
            get("/me") {
                val principal = call.principal<JWTPrincipal>()!!

                val userId = principal.subject
                    ?: throw InvalidIdentifierException("Invalid JWT Subject")

                val detail = userUseCase.getUserDetail(userId)

                call.respond(
                    HttpStatusCode.OK,
                    UserDetailResponse(
                        data = detail.toDto()
                    )
                )
            }
        }

        get("/{userId}") {
            val userId = call.parameters["userId"]
                ?: throw BadRequestException("User ID is required")

            val detail = userUseCase.getUserDetail(userId)

            call.respond(
                HttpStatusCode.OK,
                UserPublicDetailResponse(
                    data = detail.toPublicDto()
                )
            )
        }
    }
}