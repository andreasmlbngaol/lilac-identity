package com.lilac.identity.domain.service

interface MailService {
    suspend fun send(to: String, subject: String, html: String)
}