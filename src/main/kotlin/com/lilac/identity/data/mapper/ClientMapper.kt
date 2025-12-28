package com.lilac.identity.data.mapper

import com.lilac.identity.data.model.ClientEntity
import com.lilac.identity.domain.model.Client
import com.lilac.identity.util.toUUID

fun ClientEntity.toDomain() = Client(
    id = this.id.toString(),
    clientId = this.clientId,
    name = this.name,
    audience = this.audience,
    isActive = this.isActive,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)

fun Client.toEntity() = ClientEntity(
    id = this.id.toUUID(),
    clientId = this.clientId,
    name = this.name,
    audience = this.audience,
    isActive = this.isActive,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)