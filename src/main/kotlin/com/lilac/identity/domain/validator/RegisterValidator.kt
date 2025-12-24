package com.lilac.identity.domain.validator

import com.lilac.identity.domain.model.ValidationResult

interface RegisterValidator {
    fun validate(
        email: String,
        username: String,
        password: String,
        firstName: String,
        lastName: String
    ): ValidationResult
}