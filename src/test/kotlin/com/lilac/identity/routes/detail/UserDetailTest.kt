package com.lilac.identity.routes.detail

import com.lilac.identity.config.cleanupTestDatabase
import com.lilac.identity.presentation.request.RegisterRequest
import com.lilac.identity.presentation.response.TokenPairResponse
import com.lilac.identity.presentation.response.UserDetailResponse
import com.lilac.identity.presentation.response.UserPublicDetailResponse
import com.lilac.identity.testApp
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.After
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class UserDetailTest {
    @After
    fun cleanup() {
        cleanupTestDatabase()
    }

    private val registerRequest = RegisterRequest(
        email = "lgandre45@gmail.com",
        username = "andreasmlbngaol",
        password = "Test1234",
        firstName = "Andreas",
        lastName = "Manatar"
    )

    private suspend fun ApplicationTestBuilder.registerUser() = client.post("/api/auth/register") {
        setBody(registerRequest)
    }.body<TokenPairResponse>()

    @Test
    fun `get current user detail returns 200`() = testApp {
        val tokenPair = registerUser()

        val response = client.get("/api/users/me") {
            headers["Authorization"] = "Bearer ${tokenPair.data.accessToken}"
        }

        assertEquals(HttpStatusCode.OK, response.status)

        val body = response.body<UserDetailResponse>()

        assertEquals(body.data.email, registerRequest.email)
        assertEquals(body.data.username, registerRequest.username)
        assertEquals(body.data.firstName, registerRequest.firstName)
        assertEquals(body.data.lastName, registerRequest.lastName)
        assertEquals(body.data.isEmailVerified, false)
        assertNotNull(body.data.profile)
    }

    @Test
    fun `get current user public detail returns 200`() = testApp {
        val tokenPair = registerUser()

        val userDetailResponse = client.get("/api/users/me") {
            headers["Authorization"] = "Bearer ${tokenPair.data.accessToken}"
        }.body<UserDetailResponse>()

        val response = client.get("/api/users/${userDetailResponse.data.id}")

        assertEquals(HttpStatusCode.OK, response.status)

        val body = response.body<UserPublicDetailResponse>()

        assertEquals(body.data.id, userDetailResponse.data.id)
        assertEquals(body.data.username, registerRequest.username)
        assertEquals(
            body.data.name,
            "${registerRequest.firstName} ${registerRequest.lastName}"
        )
        assertNull(body.data.bio)
        assertNull(body.data.profilePictureUrl)
        assertNull(body.data.coverPictureUrl)
    }
}