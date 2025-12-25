package com.lilac.identity.data.service

import com.lilac.identity.domain.service.MailService

class TestMailServiceImpl(): MailService {
    data class SentMail(
        val to: String,
        val subject: String,
        val html: String
    )

    private val _sentMails = mutableListOf<SentMail>()
//    val sentMails: List<SentMail> get() = _sentMails

    override suspend fun send(to: String, subject: String, html: String) {
        _sentMails += SentMail(to, subject, html)
    }

}