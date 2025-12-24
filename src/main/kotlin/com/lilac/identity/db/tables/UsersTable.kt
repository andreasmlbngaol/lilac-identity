package com.lilac.identity.db.tables

import org.jetbrains.exposed.v1.core.dao.id.UUIDTable

object UsersTable: UUIDTable("users") {
    val email = varchar("email", 255).uniqueIndex()
    val username = varchar("username", 32).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val firstName = varchar("first_name", 30)
    val lastName = varchar("last_name", 30)
    val isEmailVerified = bool("is_email_verified").default(false)
    val createdAt = long("created_at").default(System.currentTimeMillis())
    val updatedAt = long("updated_at").default(System.currentTimeMillis())
}