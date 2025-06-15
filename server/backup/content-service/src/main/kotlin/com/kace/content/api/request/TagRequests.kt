package com.kace.content.api.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateTagRequest(
    val name: String,
    val slug: String
)

@Serializable
data class UpdateTagRequest(
    val name: String,
    val slug: String
) 