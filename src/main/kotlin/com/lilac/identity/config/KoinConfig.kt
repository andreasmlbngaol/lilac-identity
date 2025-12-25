package com.lilac.identity.config

import com.lilac.identity.di.repositoryModule
import com.lilac.identity.di.serviceModule
import com.lilac.identity.di.useCaseModule
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureKoin() {
    val appModule = module {
        single { loadAppConfig() }
        single { loadMailConfig() }
        single { loadAuthConfig() }
        single { loadVerificationConfig() }
        single { loadVerificationHashConfig() }
    }

    install(Koin) {
        slf4jLogger()
        modules(
            appModule,
            serviceModule,
            repositoryModule,
            useCaseModule,
        )
    }
}