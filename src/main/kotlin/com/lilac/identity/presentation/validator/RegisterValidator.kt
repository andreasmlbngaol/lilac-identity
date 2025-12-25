package com.lilac.identity.presentation.validator

import com.lilac.identity.domain.model.ValidationResult
import com.lilac.identity.presentation.request.RegisterRequest

object RegisterValidator {
    const val MIN_USERNAME_LENGTH = 3
    const val MAX_USERNAME_LENGTH = 32
    const val MIN_PASSWORD_LENGTH = 8
    const val MAX_PASSWORD_LENGTH = 100
    const val MIN_NAME_LENGTH = 2
    const val MAX_NAME_LENGTH = 30

    val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    val USERNAME_REGEX = Regex("^[a-zA-Z0-9_-]+$")

    fun validate(request: RegisterRequest): ValidationResult {
        val errors = mutableMapOf<String, String>()

        validateEmail(request.email)?.let { errors["email"] = it }
        validateUsername(request.username)?.let { errors["username"] = it }
        validatePassword(request.password)?.let { errors["password"] = it }
        validateFirstName(request.firstName)?.let { errors["firstName"] = it }
        validateLastName(request.lastName)?.let { errors["lastName"] = it }

        return if (errors.isEmpty())
            ValidationResult.Valid
        else ValidationResult.Invalid(errors)
    }

    private fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "Email cannot be blank"
            email.length > 255 -> "Email cannot be longer than 255 characters"
            !EMAIL_REGEX.matches(email) -> "Email format is invalid"
            else -> null
        }
    }

    private fun validateUsername(username: String): String? {
        return when {
            username.isBlank() -> "Username cannot be blank"
            username.length !in MIN_USERNAME_LENGTH..MAX_USERNAME_LENGTH -> "Username must be between $MIN_USERNAME_LENGTH and $MAX_USERNAME_LENGTH characters"
            !USERNAME_REGEX.matches(username) -> "Username can only contain letters, numbers, underscores and hyphens"
            else -> null
        }
    }

    private fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> "Password is required"
            password.length < MIN_PASSWORD_LENGTH -> "Password must be at least $MIN_PASSWORD_LENGTH characters"
            password.length > MAX_PASSWORD_LENGTH -> "Password must be at most $MAX_PASSWORD_LENGTH characters"
            !hasUpperCase(password) -> "Password must contain at least one uppercase letter"
            !hasLowerCase(password) -> "Password must contain at least one lowercase letter"
            !hasDigit(password) -> "Password must contain at least one digit"
            else -> null
        }
    }

    private fun validateFirstName(firstName: String): String? {
        return when {
            firstName.isBlank() -> "First name cannot be blank"
            firstName.length !in MIN_NAME_LENGTH..MAX_NAME_LENGTH -> "First name must be between $MIN_NAME_LENGTH and $MAX_NAME_LENGTH characters"
            !firstName.all { it.isLetter() || it.isWhitespace() || it == '-' } ->
                "First name can only contain letters, spaces, and hyphens"
            else -> null
        }
    }

    private fun validateLastName(lastName: String): String? {
        return when {
            lastName.isBlank() -> "Last name is required"
            lastName.length !in MIN_NAME_LENGTH..MAX_NAME_LENGTH -> "Last name must be between $MIN_NAME_LENGTH and $MAX_NAME_LENGTH characters"
            !lastName.all { it.isLetter() || it.isWhitespace() || it == '-' } ->
                "Last name can only contain letters, spaces, and hyphens"
            else -> null
        }
    }

    private fun hasUpperCase(password: String): Boolean = password.any { it.isUpperCase() }
    private fun hasLowerCase(password: String): Boolean = password.any { it.isLowerCase() }
    private fun hasDigit(password: String): Boolean = password.any { it.isDigit() }
}