package com.lilac.identity.config

import com.lilac.identity.di.repositoryModule
import com.lilac.identity.di.testServiceModule
import com.lilac.identity.di.useCaseModule
import com.lilac.identity.di.validatorModule
import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin

fun Application.configureTestKoin() {
    val testAppModule = module {
        single { loadMailConfig() }
        single { loadJwtConfig() }
        single { loadTokenConfig() }
    }

    install(Koin) {
        modules(
            testAppModule,
            testServiceModule,
            repositoryModule,
            useCaseModule,
            validatorModule
        )
    }
}