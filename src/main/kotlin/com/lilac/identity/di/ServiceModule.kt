package com.lilac.identity.di

import com.lilac.identity.data.service.JwtServiceImpl
import com.lilac.identity.data.service.MailServiceImpl
import com.lilac.identity.data.service.PasswordServiceImpl
import com.lilac.identity.data.service.TokenServiceImpl
import com.lilac.identity.domain.service.JwtService
import com.lilac.identity.domain.service.MailService
import com.lilac.identity.domain.service.PasswordService
import com.lilac.identity.domain.service.TokenService
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val serviceModule = module {
    singleOf(::JwtServiceImpl).bind<JwtService>()
    singleOf(::MailServiceImpl).bind<MailService>()
    singleOf(::PasswordServiceImpl).bind<PasswordService>()
    singleOf(::TokenServiceImpl).bind<TokenService>()
}