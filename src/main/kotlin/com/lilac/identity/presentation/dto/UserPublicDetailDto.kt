package com.lilac.identity.presentation.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserPublicDetailDto(
    val id: String,
    val username: String,
    val firstName: String,
    val lastName: String,
    val profile: UserProfileDto?
)