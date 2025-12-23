package com.lilac.identity

import com.lilac.identity.config.JwtConfig
import com.lilac.identity.config.MailConfig
import com.lilac.identity.config.configureCors
import com.lilac.identity.config.configureHealth
import com.lilac.identity.config.configureLogging
import com.lilac.identity.config.configureResponse
import com.lilac.identity.config.configureSecurity
import com.lilac.identity.config.configureSerialization
import com.lilac.identity.domain.repository.MailRepository
import com.lilac.identity.domain.repository.UserProfileRepository
import com.lilac.identity.domain.repository.UserRepository
import com.lilac.identity.domain.service.JwtService
import com.lilac.identity.domain.service.PasswordService
import com.lilac.identity.domain.usecase.AuthUseCase
import com.lilac.identity.presentation.routes.authRoutes
import io.ktor.client.HttpClient
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.dsl.module
import org.koin.logger.slf4jLogger

open class ApplicationTest {

    protected lateinit var mockUserRepository: UserRepository
    protected lateinit var mockUserProfileRepository: UserProfileRepository
    protected lateinit var mockPasswordService: PasswordService
    protected lateinit var mockMailRepository: MailRepository
    protected lateinit var mockJwtService: JwtService
    protected lateinit var mockAuthUseCase: AuthUseCase

    @BeforeEach
    fun setupTest() {
        stopKoin()

        // Initialize semua mocks
        mockUserRepository = mockk()
        mockUserProfileRepository = mockk()
        mockPasswordService = mockk()
        mockMailRepository = mockk()
        mockJwtService = mockk()
        mockAuthUseCase = mockk()
    }

    @AfterEach
    fun tearDownTest() {
        stopKoin()
    }

    protected fun testApplication(block: suspend (HttpClient) -> Unit) {
        io.ktor.server.testing.testApplication {
            application {
                moduleTest()
            }

            block(client)
        }
    }

    private fun Application.moduleTest() {
        // Jangan configure database, gunakan mocks
        configureKoinTest()
        configureLogging()
        configureSerialization()
        configureResponse()
        configureCors()
        configureSecurity()
        configureHealth()
        configureRoutingTest()
    }

    private fun Application.configureKoinTest() {
        // Buat test module dengan mocks
        val testModule = module {
            // Mock configs
            single {
                MailConfig(
                    host = "test.mail.com",
                    port = 587,
                    username = "test@mail.com",
                    password = "testpass",
                    from = "noreply@test.com"
                )
            }
            single {
                JwtConfig(
                    domain = "http://localhost:8080",
                    issuer = "test-issuer",
                    audience = "test-audience",
                    realm = "test-realm",
                    secret = "test-secret-key-that-is-long-enough",
                    accessTokenExpInMins = 10,
                    refreshTokenExpInDays = 30,
                    emailVerificationExpInMins = 10,
                    resetPasswordExpInMins = 10
                )
            }

            // Mock repositories - gunakan lazy { } agar reference yang benar
            single { lazy { mockUserRepository }.value }
            single { lazy { mockUserProfileRepository }.value }
            single { lazy { mockMailRepository }.value }

            // Mock services
            single { lazy { mockPasswordService }.value }
            single { lazy { mockJwtService }.value }

            // Mock use cases
            single { lazy { mockAuthUseCase }.value }
        }

        install(org.koin.ktor.plugin.Koin) {
            slf4jLogger()
            modules(testModule)
        }
    }

    private fun Application.configureRoutingTest() {
        routing {
            get("/") { call.respondRedirect("/api") }

            route("/api") {
                authRoutes()
            }
        }
    }
}