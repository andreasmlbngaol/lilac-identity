package com.lilac.identity.routes.register

import com.lilac.identity.config.cleanupTestDatabase
import com.lilac.identity.presentation.request.RegisterRequest
import com.lilac.identity.presentation.response.TokenPairResponse
import com.lilac.identity.testApp
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.*
import org.junit.After
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AuthRegisterTest {

    @After
    fun cleanup() {
        cleanupTestDatabase()
    }

    @Test
    fun `register success returns 201 and token pair`() = testApp {
        val response = client.post("/api/auth/register") {
            setBody(
                RegisterRequest(
                    email = "lgandre45@gmail.com ", // messy on purpose
                    username = " andreasmlbngaol ",
                    password = " Test1234 ",
                    firstName = " Andreas ",
                    lastName = " Manatar ",
                )
            )
        }

        assertEquals(HttpStatusCode.Created, response.status)

        val body = response.body<TokenPairResponse>()
        assertTrue(body.data.accessToken.isNotBlank())
        assertTrue(body.data.refreshToken.isNotBlank())
    }

    @Test
    fun `invalid payload returns 400`() = testApp {
        val response = client.post("/api/auth/register") {
            setBody(
                RegisterRequest(
                    email = "invalid-email",
                    username = "johndoe",
                    password = "SecurePass123",
                    firstName = "John",
                    lastName = "Doe",
                )
            )
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `email already used returns 409`() = testApp {
        val payload = RegisterRequest(
            email = "lgandre45@gmail.com",
            username = "andreasmlbngaol",
            password = "Test1234",
            firstName = "Andreas",
            lastName = "Manatar",
        )
        client.post("/api/auth/register") {
            setBody(payload)
        }
        val response = client.post("/api/auth/register") {
            setBody(payload)
        }

        assertEquals(HttpStatusCode.Conflict, response.status)
    }

    @Test
    fun `username already used returns 409`() = testApp {
        val payload = RegisterRequest(
            email = "lgandre45@gmail.com",
            username = "andreasmlbngaol",
            password = "Test1234",
            firstName = "Andreas",
            lastName = "Manatar",
        )
        client.post("/api/auth/register") {
            setBody(payload)
        }
        val response = client.post("/api/auth/register") {
            setBody(payload.copy(email = "andreasmlbngaol@gmail.com"))
        }

        assertEquals(HttpStatusCode.Conflict, response.status)
    }
}