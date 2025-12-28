package com.lilac.identity.routes.register

import com.lilac.identity.domain.model.ValidationResult
import com.lilac.identity.presentation.request.RegisterRequest
import com.lilac.identity.presentation.validator.RegisterValidator
import junit.framework.TestCase.assertTrue
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

class RegisterValidatorImplTest {
    private val validator = RegisterValidator

    @Test
    fun `valid payload returns Valid`() {
        val result = validator.validate(
            RegisterRequest(
                email = "john@example.com",
                username = "johndoe",
                password = "SecurePass123",
                firstName = "John",
                lastName = "Doe",
            )
        )

        assertTrue(result is ValidationResult.Valid)
    }

    @Test
    fun `invalid email returns error`() {
        val result = validator.validate(
            RegisterRequest(
                email = "invalid-email",
                username = "johndoe",
                password = "SecurePass123",
                firstName = "John",
                lastName = "Doe",
            )
        ) as ValidationResult.Invalid

        assertEquals("Email format is invalid", result.errors["email"])
    }

    @Test
    fun `password without uppercase is rejected`() {
        val result = validator.validate(
            RegisterRequest(
                email = "john@example.com",
                username = "johndoe",
                password = "securepass123",
                firstName = "John",
                lastName = "Doe",
            )
        ) as ValidationResult.Invalid

        assertEquals(
            "Password must contain at least one uppercase letter",
            result.errors["password"]
        )
    }

    @Test
    fun `first name with number is rejected`() {
        val result = validator.validate(
            RegisterRequest(
                email = "john@example.com",
                username = "johndoe",
                password = "SecurePass123",
                firstName = "John1",
                lastName = "Doe",
            )
        ) as ValidationResult.Invalid

        assertEquals(
            "First name can only contain letters, spaces, and hyphens",
            result.errors["firstName"]
        )
    }
}