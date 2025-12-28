package com.lilac.identity.routes.login

import com.lilac.identity.config.cleanupTestDatabase
import com.lilac.identity.presentation.dto.ErrorResponse
import com.lilac.identity.presentation.request.LoginRequest
import com.lilac.identity.presentation.request.RegisterRequest
import com.lilac.identity.presentation.response.TokenPairResponse
import com.lilac.identity.testApp
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.ApplicationTestBuilder
import org.junit.After
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AuthLoginTest {

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
        audience = "test-audience"
    )

    private suspend fun ApplicationTestBuilder.registerUser() {
        client.post("/api/auth/register") {
            setBody(registerRequest)
        }
    }

    @Test
    fun `login with email success returns 200 and token pair`() = testApp {
        registerUser()

        val response = client.post("/api/auth/login") {
            setBody(
                LoginRequest(
                    identifier = "lgandre45@gmail.com",
                    password = "Test1234",
                    audience = "test-audience"
                )
            )
        }

        assertEquals(HttpStatusCode.OK, response.status)

        val body = response.body<TokenPairResponse>()
        assertTrue(body.data.accessToken.isNotBlank())
        assertTrue(body.data.refreshToken.isNotBlank())
    }

    @Test
    fun `login with username success returns 200 and token pair`() = testApp {
        registerUser()

        val response = client.post("/api/auth/login") {
            setBody(
                LoginRequest(
                    identifier = "andreasmlbngaol",
                    password = "Test1234",
                    audience = "test-audience"
                )
            )
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.body<TokenPairResponse>()
        assertTrue(body.data.accessToken.isNotBlank())
        assertTrue(body.data.refreshToken.isNotBlank())
    }

    @Test
    fun `username or password invalid return 400`() = testApp {
        registerUser()

        val response = client.post("/api/auth/login") {
            setBody(
                LoginRequest(
                    identifier = "  ",
                    password = "",
                    audience = "test-audience"
                )
            )
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)

        val body = response.body<ErrorResponse>()
        assertEquals("Email or username and password are required", body.message)
    }

    @Test
    fun `user not found and return 401`() = testApp {
        registerUser()

        val response = client.post("/api/auth/login") {
            setBody(
                LoginRequest(
                    identifier = "andreasmlbngaol@gmail.com",
                    password = "invalid-password",
                    audience = "test-audience"
                )
            )
        }

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `false password returns 401`() = testApp {
        registerUser()

        val response = client.post("/api/auth/login") {
            setBody(
                LoginRequest(
                    identifier = registerRequest.email,
                    password = "falsepassword",
                    audience = "test-audience"
                )
            )
        }

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }
}