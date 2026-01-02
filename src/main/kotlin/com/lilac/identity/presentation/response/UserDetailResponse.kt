package com.lilac.identity.presentation.response

import com.lilac.identity.presentation.dto.UserDetailDto
import kotlinx.serialization.Serializable

@Serializable
data class UserDetailResponse(
    val data: UserDetailDto,
    val message: String? = "User detail retrieved successfully",
    val success: Boolean = true,
)