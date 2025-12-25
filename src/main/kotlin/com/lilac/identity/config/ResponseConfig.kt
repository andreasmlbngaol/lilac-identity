package com.lilac.identity.config

import com.lilac.identity.domain.model.CreateUserException
import com.lilac.identity.domain.model.CreateUserProfileException
import com.lilac.identity.domain.model.EmailAlreadyUsedException
import com.lilac.identity.domain.model.EmailVerificationNotSentException
import com.lilac.identity.domain.model.InternalServerException
import com.lilac.identity.domain.model.InvalidIdentifierException
import com.lilac.identity.domain.model.InvalidTokenException
import com.lilac.identity.domain.model.TokenNotProvidedException
import com.lilac.identity.domain.model.UserNotFoundException
import com.lilac.identity.domain.model.UsernameAlreadyUsedException
import com.lilac.identity.presentation.dto.ErrorResponse
import com.lilac.identity.util.respondError
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.html.respondHtml
import io.ktor.server.plugins.ContentTransformationException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import kotlinx.html.*

fun Application.configureResponse() {
    install(StatusPages) {
        exception<InvalidIdentifierException> { call, cause ->
            call.respondError(
                HttpStatusCode.Unauthorized,
                cause.message
            )
        }

        exception<InvalidTokenException> { call, _ ->
            call.respondHtml {
                head {
                    title("Invalid Token")
                    style {
                        +"""
                            body {
                                font-family: Arial, sans-serif;
                                display: flex;
                                justify-content: center;
                                align-items: center;
                                min-height: 100vh;
                                margin: 0;
                                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                            }
                            .container {
                                background: white;
                                padding: 40px;
                                border-radius: 8px;
                                box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
                                text-align: center;
                                max-width: 500px;
                            }
                            .icon {
                                font-size: 60px;
                                margin-bottom: 20px;
                            }
                            h1 {
                                color: #e74c3c;
                                margin: 0 0 10px 0;
                            }
                            p {
                                color: #666;
                                line-height: 1.6;
                            }
                        """.trimIndent()
                    }
                }
                body {
                    div(classes = "container") {
                        div(classes = "icon") { +"✕" }
                        h1 { +"Invalid or Expired Token" }
                        p { +"The verification link is invalid or has expired." }
                        p { +"Please request a new verification email." }
                    }
                }
            }
        }

        exception<TokenNotProvidedException> { call, _ ->
            call.respondHtml {
                head {
                    title("Token Not Provided")
                    style {
                        +"""
                            body {
                                font-family: Arial, sans-serif;
                                display: flex;
                                justify-content: center;
                                align-items: center;
                                min-height: 100vh;
                                margin: 0;
                                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                            }
                            .container {
                                background: white;
                                padding: 40px;
                                border-radius: 8px;
                                box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
                                text-align: center;
                                max-width: 500px;
                            }
                            .icon {
                                font-size: 60px;
                                margin-bottom: 20px;
                            }
                            h1 {
                                color: #e74c3c;
                                margin: 0 0 10px 0;
                            }
                            p {
                                color: #666;
                                line-height: 1.6;
                            }
                        """.trimIndent()
                    }
                }
                body {
                    div(classes = "container") {
                        div(classes = "icon") { +"?" }
                        h1 { +"No Verification Token" }
                        p { +"The verification link is missing a token." }
                        p { +"Please use the verification link from your email." }
                    }
                }
            }
        }

        exception<EmailAlreadyUsedException> { call, cause ->
            call.respondError(
                HttpStatusCode.Conflict,
                cause.message
            )
        }

        exception<UsernameAlreadyUsedException> { call, cause ->
            call.respondError(
                HttpStatusCode.Conflict,
                cause.message
            )
        }

        exception<EmailVerificationNotSentException> { call, cause ->
            call.respondError(
                HttpStatusCode.InternalServerError,
                cause.message
            )
        }

        exception<UserNotFoundException> { call, cause ->
            call.respondError(
                HttpStatusCode.NotFound,
                cause.message
            )
        }

        exception<CreateUserException> { call, cause ->
            call.respondError(
                HttpStatusCode.InternalServerError,
                cause.message
            )
        }

        exception<CreateUserProfileException> { call, cause ->
            call.respondError(
                HttpStatusCode.InternalServerError,
                cause.message
            )
        }

        exception<InternalServerException> { call, cause ->
            call.respondError(
                HttpStatusCode.InternalServerError,
                cause.message
            )
        }

        exception<ContentTransformationException> { call, _ ->
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse("Invalid request payload")
            )
        }

        exception<IllegalArgumentException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(cause.message ?: "Bad Request")
            )
        }

        exception<Throwable> { call, cause ->
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(cause.message ?: "Internal Server Error")
            )
        }
    }
}