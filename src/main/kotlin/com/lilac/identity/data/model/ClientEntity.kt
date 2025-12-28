package com.lilac.identity.data.model

import java.util.UUID

data class ClientEntity(
    val id: UUID,
    val clientId: String,
    val name: String,
    val audience: String,
    val isActive: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)