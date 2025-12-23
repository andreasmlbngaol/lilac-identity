package com.lilac.identity.routes

import com.lilac.identity.ApplicationTest
import com.lilac.identity.domain.model.TokenPair
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.mockk.coEvery
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import kotlin.test.Test

@DisplayName("Auth Routes Test")
class AuthRoutesTest : ApplicationTest() {

    @Test
    @DisplayName("POST /auth/register - Register berhasil")
    fun testRegisterSuccess() = testApplication { client ->
        val mockTokenPair = TokenPair(
            accessToken = "access_token_123",
            refreshToken = "refresh_token_123"
        )

        coEvery {
            mockAuthUseCase.registerUser(
                email = "test@example.com",
                username = "testuser",
                password = "password123",
                firstName = "Test",
                lastName = "User"
            )
        } returns mockTokenPair

        val response = client.post("/api/auth/register") {
            contentType(ContentType.Application.Json)
            setBody("""
                {
                    "email": "test@example.com",
                    "username": "testuser",
                    "password": "password123",
                    "firstName": "Test",
                    "lastName": "User"
                }
            """.trimIndent())
        }

        assertEquals(HttpStatusCode.Created, response.status)
        println("Response: ${response.bodyAsText()}")
    }

}