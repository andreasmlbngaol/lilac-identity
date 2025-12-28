package com.lilac.identity.db.tables

import org.jetbrains.exposed.v1.core.dao.id.UUIDTable

object ClientsTable: UUIDTable("clients") {
    val clientId = varchar("client_id", 64).uniqueIndex()
    val name = varchar("name", 64)
    val audience = varchar("audience", 64)
    val isActive = bool("is_active").default(true)
    val createdAt = long("created_at").default(System.currentTimeMillis())
    val updatedAt = long("updated_at").default(System.currentTimeMillis())
}