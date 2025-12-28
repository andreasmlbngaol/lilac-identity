package com.lilac.identity

import com.lilac.identity.config.*
import com.lilac.identity.config.TestDatabaseFactory.configureTestDatabase
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.header
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy

fun Application.testModule() {
    configureTestDatabase()
    configureTestKoin()
    startTokenCleanupJob()
    configureLogging()
    configureSerialization()
    configureResponse()
    configureCors()
    configureSecurity()
    configureHealth()
    configureRouting()
}

@OptIn(ExperimentalSerializationApi::class)
fun testApp(
    block: suspend ApplicationTestBuilder.() -> Unit
) = testApplication {
    environment {
        config = ApplicationConfig("application-test.yaml")
    }

    application {
        testModule()
    }

    client = createClient {
        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = false
                    isLenient = true
                    ignoreUnknownKeys = true
                    encodeDefaults = true
                    allowTrailingComma = true
                    namingStrategy = JsonNamingStrategy.SnakeCase
                }
            )
        }
        install(DefaultRequest) {
            contentType(ContentType.Application.Json)
            header("X-Client-Id", "test-client")
        }
    }

    block()
}