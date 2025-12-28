package com.lilac.identity.di

import com.lilac.identity.data.enum.HashAlgorithm
import com.lilac.identity.domain.usecase.AuthUseCase
import com.lilac.identity.domain.usecase.TokenCleanupUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val useCaseModule = module {
    singleOf(::TokenCleanupUseCase)
    single<AuthUseCase> {
        AuthUseCase(
            userRepository = get(),
            userProfileRepository = get(),
            verificationTokenRepository = get(),
            clientRepository = get(),
            mailRepository = get(),
            authTokenGenerator = get(),
            verificationTokenGenerator = get(),
            verificationTokenDecoder = get(),
            passwordHasher = get(named(HashAlgorithm.Bcrypt)),
            verificationTokenHasher = get(named(HashAlgorithm.HmacSha256)),
        )
    }
}