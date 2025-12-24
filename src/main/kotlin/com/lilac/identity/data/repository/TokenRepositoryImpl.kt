package com.lilac.identity.data.repository

import com.lilac.identity.data.mapper.toDomain
import com.lilac.identity.data.model.TokenEntity
import com.lilac.identity.db.tables.TokensTable
import com.lilac.identity.domain.model.Token
import com.lilac.identity.domain.enum.TokenType
import com.lilac.identity.domain.repository.TokenRepository
import com.lilac.identity.util.toUUID
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.less
import org.jetbrains.exposed.v1.jdbc.andWhere
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update

class TokenRepositoryImpl(): TokenRepository {
    private fun ResultRow.toEntity() = TokenEntity(
        id = this[TokensTable.id].value,
        userId = this[TokensTable.userId],
        tokenHashed = this[TokensTable.tokenHash],
        tokenType = this[TokensTable.type],
        isUsed = this[TokensTable.isUsed],
        usedAt = this[TokensTable.usedAt],
        issuedAt = this[TokensTable.issuedAt],
        expiresAt = this[TokensTable.expiresAt],
    )

    override suspend fun create(
        userId: String,
        tokenHash: String,
        tokenType: TokenType,
        issuedAt: Long,
        expiresAt: Long
    ): String = transaction {
        TokensTable.insertAndGetId {
            it[TokensTable.userId] = userId.toUUID()
            it[TokensTable.tokenHash] = tokenHash
            it[TokensTable.type] = tokenType
            it[TokensTable.isUsed] = false
            it[TokensTable.usedAt] = null
            it[TokensTable.issuedAt] = issuedAt
            it[TokensTable.expiresAt] = expiresAt
        }.value.toString()
    }

    override suspend fun findActiveByUserIdAndType(userId: String, tokenType: TokenType): Token? = transaction {
        TokensTable
            .selectAll()
            .where { TokensTable.userId eq userId.toUUID() }
            .andWhere { TokensTable.type eq tokenType }
            .map { it.toEntity() }
            .singleOrNull()
            ?.toDomain()
    }

    override suspend fun findByTokenHash(tokenHah: String): Token? = transaction {
        TokensTable
            .selectAll()
            .where { TokensTable.tokenHash eq tokenHah }
            .map { it.toEntity() }
            .singleOrNull()
            ?.toDomain()
    }

    override suspend fun markAsUsed(tokenId: String): Boolean = transaction {
        TokensTable
            .update({ TokensTable.id eq tokenId.toUUID() }) {
                it[TokensTable.isUsed] = true
                it[TokensTable.usedAt] = System.currentTimeMillis()
            } > 0
    }

    override suspend fun deleteByUserIdAndType(userId: String, tokenType: TokenType): Boolean = transaction {
        TokensTable
            .deleteWhere {
                (TokensTable.userId eq userId.toUUID()) and
                        (TokensTable.type eq tokenType)
            } > 0
    }

    override suspend fun deleteExpiredTokens(): Int = transaction {
        TokensTable
            .deleteWhere { TokensTable.expiresAt less System.currentTimeMillis() }
    }
}