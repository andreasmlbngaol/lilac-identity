package com.lilac.identity.domain.usecase

import com.lilac.identity.domain.enum.VerificationTokenType
import com.lilac.identity.domain.model.*
import com.lilac.identity.domain.repository.ClientRepository
import com.lilac.identity.domain.repository.MailRepository
import com.lilac.identity.domain.repository.UserProfileRepository
import com.lilac.identity.domain.repository.UserRepository
import com.lilac.identity.domain.repository.VerificationTokenRepository
import com.lilac.identity.domain.service.AuthTokenGenerator
import com.lilac.identity.domain.service.Hasher
import com.lilac.identity.domain.service.VerificationTokenDecoder
import com.lilac.identity.domain.service.VerificationTokenGenerator
import com.lilac.identity.util.toMinutes

class AuthUseCase(
    private val userRepository: UserRepository,
    private val userProfileRepository: UserProfileRepository,
    private val verificationTokenRepository: VerificationTokenRepository,
    private val clientRepository: ClientRepository,
    private val mailRepository: MailRepository,
    private val authTokenGenerator: AuthTokenGenerator,
    private val verificationTokenGenerator: VerificationTokenGenerator,
    private val verificationTokenDecoder: VerificationTokenDecoder,
    private val passwordHasher: Hasher,
    private val verificationTokenHasher: Hasher
) {
    suspend fun register(
        email: String,
        username: String,
        password: String,
        firstName: String,
        lastName: String,
        clientId: String
    ): TokenPair {
        val audience = clientRepository.findAudienceByClientId(clientId)
            ?: throw InvalidIdentifierException("Invalid Client ID")

        val emailUsed = userRepository.existsByEmail(email)
        if (emailUsed) throw EmailAlreadyUsedException()

        val usernameUsed = userRepository.existsByUsername(username)
        if (usernameUsed) throw UsernameAlreadyUsedException()

        val passwordHash = passwordHasher.hash(password)

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

        val verificationToken = verificationTokenGenerator.generateEmailVerificationToken(userId)

        verificationTokenRepository.create(
            userId,
            tokenHash = verificationTokenHasher.hash(verificationToken.token),
            verificationTokenType = VerificationTokenType.EmailVerification,
            issuedAt = verificationToken.issuedAt,
            expiresAt = verificationToken.expiresAt
        )

        val expiresInMin = (verificationToken.expiresAt - verificationToken.issuedAt).toMinutes()

        val emailSent = mailRepository.sendEmailVerification(
            email = email,
            fullName = "$firstName $lastName",
            token = verificationToken.token,
            expiresInMin = expiresInMin
        )

        if (!emailSent) {
            userRepository.deleteById(userId)
            throw EmailVerificationNotSentException()
        }

        val user = userRepository.findById(userId) ?: throw UserNotFoundException()

        return generateTokenPair(
            user = user,
            audience = audience
        )
    }

    suspend fun login(
        emailOrUsername: String,
        password: String,
        clientId: String
    ): TokenPair {
        val audience = clientRepository.findAudienceByClientId(clientId)
            ?: throw InvalidIdentifierException("Invalid Client ID")

        val user = userRepository.findByEmailOrUsername(emailOrUsername)
            ?: throw InvalidIdentifierException()

        passwordHasher.verify(password, user.passwordHash).let { verified ->
            if (!verified) throw InvalidIdentifierException()
        }

        return generateTokenPair(
            user = user,
            audience = audience
        )
    }

    suspend fun verifyEmail(token: String): Boolean {
        val decoded = verificationTokenDecoder.decodeEmailVerificationToken(token)
            ?: throw InvalidTokenException("Invalid Token")
        val userId = decoded.userId

        val tokenHash = verificationTokenHasher.hash(token)
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

    private fun generateTokenPair(
        user: User,
        audience: String
    ): TokenPair {
        val accessToken = authTokenGenerator.generateAccessToken(
            userId = user.id,
            username = user.username,
            firstName = user.firstName,
            lastName = user.lastName,
            isEmailVerified = user.isEmailVerified,
            audience = audience
        )

        val refreshToken = authTokenGenerator.generateRefreshToken(
            userId = user.id,
            audience = audience
        )

        return TokenPair(accessToken.token, refreshToken.token)
    }

    //    suspend fun forgotPassword(emailOrUsername: String): Boolean {
//        val user = userRepository.findByEmailOrUsername(emailOrUsername)
//            ?: throw UserNotFoundException()
//
//        val issuedAt = System.currentTimeMillis()
//        val expiresAt = authTokenService.passwordResetExpInMin * 60 * 1000 + issuedAt
//        val token = authTokenService.generatePasswordResetToken(
//            userId = user.id,
//            issuedAt = issuedAt,
//            expiresAt = expiresAt
//        )
//
//        return mailRepository.sendPasswordResetEmail(
//            email = user.email,
//            fullName = "${user.firstName} ${user.lastName}",
//            link = "${authTokenService.domain}/api/auth/reset-password?token=$token",
//            expiresInMin = authTokenService.passwordResetExpInMin
//        )
//    }
//
//    suspend fun resetPassword(token: String, newPassword: String): Boolean {
//        val decoded = authTokenService.decodePasswordResetToken(token) ?: throw InvalidTokenException()
//        val userId = decoded.subject
//        val hash = passwordHasher.hash(newPassword)
//        return userRepository.updatePassword(userId, hash)
//    }
}