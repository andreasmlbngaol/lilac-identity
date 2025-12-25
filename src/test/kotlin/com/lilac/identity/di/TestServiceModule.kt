package com.lilac.identity.di

import com.lilac.identity.data.enum.HashAlgorithm
import com.lilac.identity.data.service.BCryptHasher
import com.lilac.identity.data.service.TestMailServiceImpl
import com.lilac.identity.data.service.HmacSHA256Hasher
import com.lilac.identity.data.service.JwtAuthTokenService
import com.lilac.identity.data.service.JwtVerificationTokenService
import com.lilac.identity.domain.service.AuthTokenDecoder
import com.lilac.identity.domain.service.AuthTokenGenerator
import com.lilac.identity.domain.service.Hasher
import com.lilac.identity.domain.service.MailService
import com.lilac.identity.domain.service.VerificationTokenDecoder
import com.lilac.identity.domain.service.VerificationTokenGenerator
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module

val testServiceModule = module {
    singleOf(::TestMailServiceImpl).bind<MailService>()

    singleOf(::JwtAuthTokenService).binds(
        arrayOf(
            AuthTokenGenerator::class,
            AuthTokenDecoder::class
        )
    )

    singleOf(::JwtVerificationTokenService).binds(
        arrayOf(
            VerificationTokenGenerator::class,
            VerificationTokenDecoder::class
        )
    )

    single<Hasher>(named(HashAlgorithm.Bcrypt)) {
        BCryptHasher()
    }
    single<Hasher>(named(HashAlgorithm.HmacSha256)) {
        HmacSHA256Hasher(get())
    }
}