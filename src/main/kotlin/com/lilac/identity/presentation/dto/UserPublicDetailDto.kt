package com.lilac.identity.presentation.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserPublicDetailDto(
    val id: String,
    val username: String,
    val name: String,
    val bio: String?,
    val profilePictureUrl: String?,
    val coverPictureUrl: String?,
)