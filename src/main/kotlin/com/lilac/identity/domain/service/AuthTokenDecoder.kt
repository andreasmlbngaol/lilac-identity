package com.lilac.identity.domain.service

import com.lilac.identity.domain.model.AccessTokenClaims
import com.lilac.identity.domain.model.RefreshTokenClaims

interface AuthTokenDecoder {
    fun decodeAccessToken(token: String): AccessTokenClaims?
    fun decodeRefreshToken(token: String): RefreshTokenClaims?
}