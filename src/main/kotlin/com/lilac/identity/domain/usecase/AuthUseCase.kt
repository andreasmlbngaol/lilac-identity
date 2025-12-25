package com.lilac.identity.domain.usecase

import com.lilac.identity.domain.enum.VerificationTokenType
import com.lilac.identity.domain.model.*
import com.lilac.identity.domain.repository.MailRepository
import com.lilac.identity.domain.repository.VerificationTokenRepository
import com.lilac.identity.domain.repository.UserProfileRepository
import com.lilac.identity.domain.repository.UserRepository
import com.lilac.identity.domain.service.AuthTokenService
import com.lilac.identity.domain.service.PasswordService
import com.lilac.identity.domain.service.VerificationTokenService

class AuthUseCase(
    private val userRepository: UserRepository,
    private val userProfileRepository: UserProfileRepository,
    private val verificationTokenRepository: VerificationTokenRepository,
    private val mailRepository: MailRepository,
    private val authTokenService: AuthTokenService,
    private val passwordService: PasswordService,
    private val verificationTokenService: VerificationTokenService
) {
    suspend fun register(
        email: String,
        username: String,
        password: String,
        firstName: String,
        lastName: String
    ): TokenPair {
        val emailUsed = userRepository.existsByEmail(email)
        if (emailUsed) throw EmailAlreadyUsedException()

        val usernameUsed = userRepository.existsByUsername(username)
        if (usernameUsed) throw UsernameAlreadyUsedException()

        val passwordHash = passwordService.hash(password)

        val userId = userRepository.create(
            email = email,
            username = username,
            passwordHash = passwordHash,
            firstName = firstName,
            lastName = lastName,
        )

        userProfileRepository.create(
            userId = userId,
            bio = null,
            profilePictureUrl = null,
            coverPictureUrl = null
        )

        val issuedAt = System.currentTimeMillis()
        val expiresAt = authTokenService.emailVerificationExpInMin * 60 * 1000 + issuedAt
        val token = authTokenService.generateEmailVerificationToken(
            userId = userId,
            issuedAt = issuedAt,
            expiresAt = expiresAt
        )

        val tokenHash = verificationTokenService.hash(token)
        verificationTokenRepository.create(
            userId,
            tokenHash = tokenHash,
            verificationTokenType = VerificationTokenType.EmailVerification,
            issuedAt = issuedAt,
            expiresAt = expiresAt
        )

        val emailSent = mailRepository.sendEmailVerification(
            email = email,
            fullName = "$firstName $lastName",
            link = "${authTokenService.domain}/api/auth/verify-email?token=$token",
            expiresInMin = authTokenService.emailVerificationExpInMin
        )

        if (!emailSent) {
            userRepository.deleteById(userId)
            throw EmailVerificationNotSentException()
        }

        val user = userRepository.findById(userId) ?: throw UserNotFoundException()

        return generateTokenPair(user)
    }

    suspend fun login(emailOrUsername: String, password: String): TokenPair {
        val user = userRepository.findByEmailOrUsername(emailOrUsername)
            ?: throw InvalidIdentifierException()

        passwordService.verify(password, user.passwordHash).let { verified ->
            if (!verified) throw InvalidIdentifierException()
        }

        return generateTokenPair(user)
    }

    suspend fun verifyEmail(token: String): Boolean {
        val decoded = authTokenService.decodeEmailVerificationToken(token)
            ?: throw InvalidTokenException("Invalid Token")
        val userId = decoded.subject

        val tokenHash = verificationTokenService.hash(token)
        val tokenEntity = verificationTokenRepository.findByTokenHash(tokenHash)
            ?: throw InvalidTokenException("Token not found")

        if (tokenEntity.isUsed) {
            throw InvalidTokenException("Token has already used")
        }

        val exists = userRepository.existsById(userId)
        if(!exists) throw InvalidTokenException("User not found")

        verificationTokenRepository.markAsUsed(tokenEntity.id)

        return userRepository.markEmailVerified(userId)
    }

    suspend fun forgotPassword(emailOrUsername: String): Boolean {
        val user = userRepository.findByEmailOrUsername(emailOrUsername)
            ?: throw UserNotFoundException()

        val issuedAt = System.currentTimeMillis()
        val expiresAt = authTokenService.passwordResetExpInMin * 60 * 1000 + issuedAt
        val token = authTokenService.generatePasswordResetToken(
            userId = user.id,
            issuedAt = issuedAt,
            expiresAt = expiresAt
        )

        return mailRepository.sendPasswordResetEmail(
            email = user.email,
            fullName = "${user.firstName} ${user.lastName}",
            link = "${authTokenService.domain}/api/auth/reset-password?token=$token",
            expiresInMin = authTokenService.passwordResetExpInMin
        )
    }

    suspend fun resetPassword(token: String, newPassword: String): Boolean {
        val decoded = authTokenService.decodePasswordResetToken(token) ?: throw InvalidTokenException()
        val userId = decoded.subject
        val hash = passwordService.hash(newPassword)
        return userRepository.updatePassword(userId, hash)
    }

    private fun generateTokenPair(user: User): TokenPair {
        val accessToken = authTokenService.generateAccessToken(
            userId = user.id,
            username = user.username,
            firstName = user.firstName,
            lastName = user.lastName,
            isEmailVerified = user.isEmailVerified
        )

        val refreshToken = authTokenService.generateRefreshToken(user.id)

        return TokenPair(accessToken, refreshToken)
    }
}