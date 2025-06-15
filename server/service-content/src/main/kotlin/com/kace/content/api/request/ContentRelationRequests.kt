package com.kace.content.api.request

import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class CreateContentRelationRequest(
    val sourceId: UUID,
    val targetId: UUID,
    val relationType: String,
    val metadata: Map<String, String>? = null
) 