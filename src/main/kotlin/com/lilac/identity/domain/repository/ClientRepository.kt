package com.lilac.identity.domain.repository

interface ClientRepository {
    suspend fun findAudienceByClientId(clientId: String): String?
}