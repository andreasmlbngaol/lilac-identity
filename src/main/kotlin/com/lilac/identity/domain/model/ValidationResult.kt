package com.lilac.identity.domain.model

sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val errors: Map<String, String>): ValidationResult()
}