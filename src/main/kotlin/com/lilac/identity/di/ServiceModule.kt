package com.lilac.identity.di

import com.lilac.identity.data.service.AuthTokenServiceImpl
import com.lilac.identity.data.service.MailServiceImpl
import com.lilac.identity.data.service.PasswordServiceImpl
import com.lilac.identity.data.service.VerificationTokenServiceImpl
import com.lilac.identity.domain.service.AuthTokenService
import com.lilac.identity.domain.service.MailService
import com.lilac.identity.domain.service.PasswordService
import com.lilac.identity.domain.service.VerificationTokenService
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val serviceModule = module {
    singleOf(::AuthTokenServiceImpl).bind<AuthTokenService>()
    singleOf(::MailServiceImpl).bind<MailService>()
    singleOf(::PasswordServiceImpl).bind<PasswordService>()
    singleOf(::VerificationTokenServiceImpl).bind<VerificationTokenService>()
}