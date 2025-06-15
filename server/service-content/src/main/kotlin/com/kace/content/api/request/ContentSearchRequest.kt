package com.kace.content.api.request

import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.*

@Serializable
data class ContentSearchRequest(
    val query: String? = null,
    val page: Int? = 0,
    val size: Int? = 20,
    val contentTypeId: UUID? = null,
    val authorId: UUID? = null,
    val categoryId: UUID? = null,
    val tagId: UUID? = null,
    val status: String? = null,
    val fromDate: LocalDateTime? = null,
    val toDate: LocalDateTime? = null,
    val sortBy: String? = "updatedAt",
    val sortDirection: String? = "DESC"
) 