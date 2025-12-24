package com.lilac.identity.config

import com.lilac.identity.domain.usecase.TokenCleanupUseCase
import io.ktor.server.application.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.ktor.plugin.koin
import java.util.concurrent.TimeUnit

fun Application.startTokenCleanupJob(
    intervalMinutes: Long = 30
) {
    val tokenCleanupUseCase = koin().get<TokenCleanupUseCase>()
    val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    scope.launch {
        while(isActive) {
            try {
                val deletedCount = tokenCleanupUseCase.cleanupExpiredToken()
                println("Token cleanup job: Deleted $deletedCount expired tokens")
            } catch (e: Exception) {
                e.printStackTrace()
                println("Token cleanup job failed: ${e.message}")
            }
            delay(TimeUnit.MINUTES.toMillis(intervalMinutes))
        }
    }
}