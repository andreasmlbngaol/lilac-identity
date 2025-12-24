package com.lilac.identity.presentation.routes

import com.lilac.identity.domain.model.TokenNotProvidedException
import com.lilac.identity.domain.usecase.AuthUseCase
import io.ktor.server.html.respondHtml
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import kotlinx.html.*

fun Route.verifyEmailRoutes(authUseCase: AuthUseCase) {
    get("/verify-email") {
        val verifyToken = call.parameters["token"]
            ?: throw TokenNotProvidedException()

        val success = authUseCase.verifyEmail(verifyToken)
        if(success) {
            call.respondHtml {
                head {
                    title("Email Verified")
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
                                    color: #333;
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
                        div(classes = "icon") { +"✓" }
                        h1 { +"Email Verified!" }
                        p { +"Your email has been successfully verified." }
                        p { +"You can continue using the application now." }
                    }
                }
            }
        }
    }
}
