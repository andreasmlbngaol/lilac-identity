package com.lilac.identity.domain.service

interface Hasher {
    fun hash(content: String): String
    fun verify(content: String, hash: String): Boolean
}