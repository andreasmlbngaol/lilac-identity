package com.lilac.identity.config

import com.lilac.identity.di.repositoryModule
import com.lilac.identity.di.testServiceModule
import com.lilac.identity.di.useCaseModule
import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin

fun Application.configureTestKoin() {
    val testAppModule = module {
        single { loadAppConfig() }
        single { loadMailConfig() }
        single { loadAuthConfig() }
        single { loadVerificationConfig() }
        single { loadVerificationHashConfig() }
    }

    install(Koin) {
        modules(
            testAppModule,
            testServiceModule,
            repositoryModule,
            useCaseModule
        )
    }
}