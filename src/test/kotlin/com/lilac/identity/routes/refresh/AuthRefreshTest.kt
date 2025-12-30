package com.lilac.identity.routes.refresh

import com.lilac.identity.config.cleanupTestDatabase
import com.lilac.identity.presentation.request.LoginRequest
import com.lilac.identity.presentation.request.RefreshTokenRequest
import com.lilac.identity.presentation.request.RegisterRequest
import com.lilac.identity.presentation.response.TokenPairResponse
import com.lilac.identity.testApp
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.ApplicationTestBuilder
import org.junit.After
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AuthRefreshTest {

    @After
    fun cleanup() {
        cleanupTestDatabase()
    }

    private val registerRequest = RegisterRequest(
        email = "lgandre45@gmail.com",
        username = "andreasmlbngaol",
        password = "Test1234",
        firstName = "Andreas",
        lastName = "Manatar",
    )

    private val loginRequest = LoginRequest(
        identifier = "andreasmlbngaol",
        password = "Test1234"
    )

    private suspend fun ApplicationTestBuilder.registerUser() {
        client.post("/api/auth/register") {
            setBody(registerRequest)
        }
    }

    private suspend fun ApplicationTestBuilder.loginUser(): TokenPairResponse {
        return client.post("/api/auth/login") {
            setBody(loginRequest)
        }.body<TokenPairResponse>()
    }

    @Test
    fun `refresh token returns 200 and token pair`() = testApp {
        registerUser()
        val tokenPair = loginUser()

        val response = client.post("/api/auth/refresh") {
            setBody(
                RefreshTokenRequest(
                    refreshToken = tokenPair.data.refreshToken
                )
            )
        }

        println("Status: ${response.status}")

        assertEquals(HttpStatusCode.OK, response.status)
        println("Body as Text: ${response.bodyAsText()}")
        val body = response.body<TokenPairResponse>()
        println("Body: $body")
        assertTrue(body.data.accessToken.isNotBlank())
        assertTrue(body.data.refreshToken.isNotBlank())
    }

}