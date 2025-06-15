package com.kace.content.api.response

import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.*

@Serializable
data class ContentPermissionResponse(
    val id: UUID,
    val contentId: UUID,
    val principalId: String,
    val principalType: String,
    val permission: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) 