package com.lilac.identity.di

import com.lilac.identity.data.validator.RegisterValidatorImpl
import com.lilac.identity.domain.validator.RegisterValidator
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val validatorModule = module {
    singleOf(::RegisterValidatorImpl).bind<RegisterValidator>()
}