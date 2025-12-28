package com.lilac.identity.data.repository

import com.lilac.identity.data.mapper.toDomain
import com.lilac.identity.data.model.ClientEntity
import com.lilac.identity.db.tables.ClientsTable
import com.lilac.identity.domain.repository.ClientRepository
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.andWhere
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class ClientRepositoryImpl(): ClientRepository {
    private fun ResultRow.toEntity() = ClientEntity(
        id = this[ClientsTable.id].value,
        clientId = this[ClientsTable.clientId],
        name = this[ClientsTable.name],
        audience = this[ClientsTable.audience],
        isActive = this[ClientsTable.isActive],
        createdAt = this[ClientsTable.createdAt],
        updatedAt = this[ClientsTable.updatedAt]
    )

    override suspend fun findAudienceByClientId(clientId: String): String? = transaction {
        ClientsTable
            .selectAll()
            .where { ClientsTable.clientId eq clientId }
            .andWhere { ClientsTable.isActive eq true }
            .map { it.toEntity() }
            .singleOrNull()
            ?.toDomain()
            ?.audience
    }
}