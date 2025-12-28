package com.lilac.identity.domain.model

data class Client(
    val id: String,
    val clientId: String,
    val name: String,
    val audience: String,
    val isActive: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)
