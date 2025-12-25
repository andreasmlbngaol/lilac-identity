package com.lilac.identity.data.service

import com.lilac.identity.config.TokenConfig
import com.lilac.identity.domain.service.VerificationTokenService
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class VerificationTokenServiceImpl(
    private val config: TokenConfig
): VerificationTokenService {
    override fun hash(token: String): String {
        return hmacSha256(token)
    }

    override fun verify(token: String, hash: String): Boolean {
        val tokenHash = hmacSha256(token)
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