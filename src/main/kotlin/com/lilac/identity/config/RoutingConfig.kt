package com.lilac.identity.config

import com.lilac.identity.presentation.response.HelloResponse
import com.lilac.identity.presentation.routes.authRoutes
import com.lilac.identity.presentation.routes.userRoutes
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.openapi.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") { call.respondRedirect("/api") }
        route("/api") {
            openAPI(path="openapi", swaggerFile = "openapi/documentation.json")
            swaggerUI(path="swagger", swaggerFile = "openapi/documentation.json")

            get {
                call.respond(
                    HttpStatusCode.OK,
                    HelloResponse()
                )
            }

            authRoutes()
            userRoutes()
        }
    }
}