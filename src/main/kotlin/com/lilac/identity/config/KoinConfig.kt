package com.lilac.identity.config

import com.lilac.identity.di.repositoryModule
import com.lilac.identity.di.serviceModule
import com.lilac.identity.di.useCaseModule
import com.lilac.identity.di.validatorModule
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureKoin() {
    val appModule = module {
        single { loadMailConfig() }
        single { loadJwtConfig() }
        single { loadTokenConfig() }
    }

    install(Koin) {
        slf4jLogger()
        modules(
            appModule,
            serviceModule,
            repositoryModule,
            useCaseModule,
            validatorModule
        )
    }
}