package com.kace.content.api.request

import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class CreateContentPermissionRequest(
    val contentId: UUID,
    val principalId: String,
    val principalType: String,
    val permission: String
)

@Serializable
data class UpdateContentPermissionRequest(
    val permission: String
) 