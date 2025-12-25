package com.lilac.identity.data.service

import com.lilac.identity.config.VerificationHashConfig
import com.lilac.identity.domain.service.Hasher
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class HmacSHA256Hasher(
    private val config: VerificationHashConfig
): Hasher {
    override fun hash(content: String): String {
        return hmacSha256(content)
    }

    override fun verify(content: String, hash: String): Boolean {
        val tokenHash = hmacSha256(content)
        return tokenHash == hash
    }

    private fun hmacSha256(message: String): String {
        val secretKeySpec = SecretKeySpec(config.secret.toByteArray(), "HmacSHA256")
        val mac = Mac.getInstance("HmacSHA256").apply {
            init(secretKeySpec)
        }
        val hashBytes = mac.doFinal(message.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}