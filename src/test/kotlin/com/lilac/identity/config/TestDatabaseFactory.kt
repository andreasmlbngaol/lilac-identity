package com.lilac.identity.config

import com.lilac.identity.db.tables.TokensTable
import com.lilac.identity.db.tables.UserProfilesTable
import com.lilac.identity.db.tables.UsersTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
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
    }

    fun createTestTables() = transaction {
        SchemaUtils.create(
            UsersTable,
            UserProfilesTable,
            TokensTable
        )
    }

    fun clearTestDatabase() = transaction {
        SchemaUtils.drop(
            TokensTable,
            UserProfilesTable,
            UsersTable
        )
    }
}

fun cleanupTestDatabase() {
    TestDatabaseFactory.clearTestDatabase()
}