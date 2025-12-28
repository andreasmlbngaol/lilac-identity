package com.lilac.identity.presentation.request

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val identifier: String,
    val password: String,
    val audience: String
)
