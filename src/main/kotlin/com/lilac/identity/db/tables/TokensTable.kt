package com.lilac.identity.db.tables

import com.lilac.identity.domain.enum.TokenType
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.UUIDTable

object TokensTable: UUIDTable("tokens") {
    val userId = uuid("user_id").references(UsersTable.id, onDelete = ReferenceOption.CASCADE)
    val tokenHash = varchar("token_hash", 255).uniqueIndex()
    val type = enumerationByName<TokenType>("type", 32)
    val isUsed = bool("is_used").default(false)
    val usedAt = long("used_at").nullable().default(null)
    val issuedAt = long("created_at").default(System.currentTimeMillis())
    val expiresAt = long("expires_at")

    init {
        uniqueIndex(userId, type)
    }
}