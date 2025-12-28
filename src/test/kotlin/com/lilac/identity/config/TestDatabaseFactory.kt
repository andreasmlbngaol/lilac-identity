package com.lilac.identity.config

import com.lilac.identity.db.tables.ClientsTable
import com.lilac.identity.db.tables.VerificationTokensTable
import com.lilac.identity.db.tables.UserProfilesTable
import com.lilac.identity.db.tables.UsersTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

@Suppress("unused")
object TestDatabaseFactory {
    fun configureTestDatabase() {
        val config = HikariConfig().apply {
            jdbcUrl = "jdbc:h2:mem:test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE"
            driverClassName = "org.h2.Driver"
            username = "sa"
            password = ""
            maximumPoolSize = 3
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        }

        Database.connect(HikariDataSource(config))
        createTestTables()
        seedClients()
    }

    fun createTestTables() = transaction {
        SchemaUtils.create(
            UsersTable,
            UserProfilesTable,
            VerificationTokensTable,
            ClientsTable
        )
    }

    fun seedClients() = transaction {
        ClientsTable
            .insert {
                it[clientId] = "test-client"
                it[name] = "Test Client"
                it[audience] = "test-audience"
                it[isActive] = true
                it[createdAt] = System.currentTimeMillis()
                it[updatedAt] = System.currentTimeMillis()
            }
    }

    fun clearTestDatabase() = transaction {
        SchemaUtils.drop(
            VerificationTokensTable,
            UserProfilesTable,
            UsersTable,
            ClientsTable
        )
    }
}

fun cleanupTestDatabase() {
    TestDatabaseFactory.clearTestDatabase()
}