package com.lilac.identity.data.repository

import com.lilac.identity.config.AppConfig
import com.lilac.identity.domain.repository.MailRepository
import com.lilac.identity.domain.service.MailService
import kotlinx.html.*
import kotlinx.html.stream.createHTML

class MailRepositoryImpl(
    private val mailService: MailService,
    private val appConfig: AppConfig,
): MailRepository {
    override suspend fun sendEmailVerification(
        email: String,
        fullName: String,
        token: String,
        expiresInMin: Long
    ): Boolean {
        return try {
            val subject = "Verify your email"

            val body = createHTML().html {
                body {
                    p { +"Hi $fullName," }
                    p { +"Please verify your email by clicking the button below:" }
                    p {
                        a(href = "${appConfig.domain}/api/auth/verify-email?token=$token") {
                            style = "display:inline-block;padding:10px 20px;background-color:#8e44ad;color:white;text-decoration:none;border-radius:4px;"
                            +"Verify Email"
                        }
                    }
                    p { +"This link will expire in $expiresInMin minutes." }
                }
            }

            mailService.send(
                to = email,
                subject = subject,
                html = body
            )

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun sendPasswordResetEmail(
        email: String,
        fullName: String,
        link: String,
        expiresInMin: Long
    ): Boolean {
        return try {
            val subject = "Reset your password"

            val body = createHTML().html {
                body {
                    p { +"Hi $fullName," }
                    p { +"Click below to reset your password:" }
                    p {
                        a(href = link) {
                            style = "display:inline-block;padding:10px 20px;background-color:#8e44ad;color:white;text-decoration:none;border-radius:4px;"
                            +"Reset Password"
                        }
                    }
                    p { +"This link will expire in $expiresInMin minutes." }
                }
            }

            mailService.send(
                to = email,
                subject = subject,
                html = body
            )

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun sendWelcomeEmail(email: String, fullName: String): Boolean {
        return try {
            val html = """
                <html><body>
                <p>Hi $fullName,</p>
                <p>Welcome to Lilac!</p>
                </body></html>
            """.trimIndent()

            mailService.send(email, "Welcome!", html)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}