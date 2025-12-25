package com.lilac.identity.data.repository

import com.lilac.identity.data.mapper.toDomain
import com.lilac.identity.data.model.VerificationTokenEntity
import com.lilac.identity.db.tables.VerificationTokensTable
import com.lilac.identity.domain.model.VerificationToken
import com.lilac.identity.domain.enum.VerificationTokenType
import com.lilac.identity.domain.repository.VerificationTokenRepository
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

class VerificationTokenRepositoryImpl(): VerificationTokenRepository {
    private fun ResultRow.toEntity() = VerificationTokenEntity(
        id = this[VerificationTokensTable.id].value,
        userId = this[VerificationTokensTable.userId],
        tokenHashed = this[VerificationTokensTable.tokenHash],
        verificationTokenType = this[VerificationTokensTable.type],
        isUsed = this[VerificationTokensTable.isUsed],
        usedAt = this[VerificationTokensTable.usedAt],
        issuedAt = this[VerificationTokensTable.issuedAt],
        expiresAt = this[VerificationTokensTable.expiresAt],
    )

    override suspend fun create(
        userId: String,
        tokenHash: String,
        verificationTokenType: VerificationTokenType,
        issuedAt: Long,
        expiresAt: Long
    ): String = transaction {
        VerificationTokensTable.insertAndGetId {
            it[VerificationTokensTable.userId] = userId.toUUID()
            it[VerificationTokensTable.tokenHash] = tokenHash
            it[VerificationTokensTable.type] = verificationTokenType
            it[VerificationTokensTable.isUsed] = false
            it[VerificationTokensTable.usedAt] = null
            it[VerificationTokensTable.issuedAt] = issuedAt
            it[VerificationTokensTable.expiresAt] = expiresAt
        }.value.toString()
    }

    override suspend fun findActiveByUserIdAndType(userId: String, verificationTokenType: VerificationTokenType): VerificationToken? = transaction {
        VerificationTokensTable
            .selectAll()
            .where { VerificationTokensTable.userId eq userId.toUUID() }
            .andWhere { VerificationTokensTable.type eq verificationTokenType }
            .map { it.toEntity() }
            .singleOrNull()
            ?.toDomain()
    }

    override suspend fun findByTokenHash(tokenHah: String): VerificationToken? = transaction {
        VerificationTokensTable
            .selectAll()
            .where { VerificationTokensTable.tokenHash eq tokenHah }
            .map { it.toEntity() }
            .singleOrNull()
            ?.toDomain()
    }

    override suspend fun markAsUsed(tokenId: String): Boolean = transaction {
        VerificationTokensTable
            .update({ VerificationTokensTable.id eq tokenId.toUUID() }) {
                it[VerificationTokensTable.isUsed] = true
                it[VerificationTokensTable.usedAt] = System.currentTimeMillis()
            } > 0
    }

    override suspend fun deleteByUserIdAndType(userId: String, verificationTokenType: VerificationTokenType): Boolean = transaction {
        VerificationTokensTable
            .deleteWhere {
                (VerificationTokensTable.userId eq userId.toUUID()) and
                        (VerificationTokensTable.type eq verificationTokenType)
            } > 0
    }

    override suspend fun deleteExpiredTokens(): Int = transaction {
        VerificationTokensTable
            .deleteWhere { VerificationTokensTable.expiresAt less System.currentTimeMillis() }
    }
}