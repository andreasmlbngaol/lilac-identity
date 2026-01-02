package com.lilac.identity.presentation.response

import com.lilac.identity.presentation.dto.UserPublicDetailDto
import kotlinx.serialization.Serializable

@Serializable
data class UserPublicDetailResponse(
    val data: UserPublicDetailDto,
    val success: Boolean = true,
    val message: String? = "User ${data.id} detail retrieved successfully"
)