package com.lilac.identity.domain.service

interface TokenService {
    fun hash(token: String): String
    fun verify(token: String, hash: String): Boolean
}