package com.lilac.identity.di

import com.lilac.identity.domain.usecase.AuthUseCase
import com.lilac.identity.domain.usecase.TokenCleanupUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val useCaseModule = module {
    singleOf(::AuthUseCase)
    singleOf(::TokenCleanupUseCase)
}