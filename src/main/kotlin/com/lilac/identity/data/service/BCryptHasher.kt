package com.lilac.identity.data.service

import com.lilac.identity.domain.service.Hasher
import org.mindrot.jbcrypt.BCrypt

class BCryptHasher(): Hasher {
    override fun hash(content: String): String =
        BCrypt.hashpw(content, BCrypt.gensalt())
    override fun verify(content: String, hash: String) =
        BCrypt.checkpw(content, hash)
}