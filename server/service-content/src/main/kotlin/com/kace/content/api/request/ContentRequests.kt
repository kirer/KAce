package com.kace.content.api.request

import com.kace.content.domain.model.ContentStatus
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class CreateContentRequest(
    val contentTypeId: String,
    val title: String,
    val slug: String,
    val fields: Map<String, @Serializable(with = AnySerializer::class) Any>,
    val status: String = "DRAFT",
    val languageCode: String = "zh-CN"
)

@Serializable
data class UpdateContentRequest(
    val title: String,
    val slug: String,
    val fields: Map<String, @Serializable(with = AnySerializer::class) Any>,
    val status: String,
    val comment: String = "Updated content"
)

@Serializable
data class PublishContentRequest(
    val comment: String = "Published content"
)

@Serializable
data class UnpublishContentRequest(
    val comment: String = "Unpublished content"
)

@Serializable
data class AddContentCategoryRequest(
    val categoryId: String
)

@Serializable
data class AddContentTagRequest(
    val tagId: String
)

@Serializable
data class AddContentTagsRequest(
    val tags: List<String>
)

@kotlinx.serialization.Serializer(forClass = Any::class)
object AnySerializer : kotlinx.serialization.KSerializer<Any> {
    override val descriptor: kotlinx.serialization.descriptors.SerialDescriptor =
        kotlinx.serialization.descriptors.PrimitiveSerialDescriptor("Any", kotlinx.serialization.descriptors.PrimitiveKind.STRING)
    
    override fun serialize(encoder: kotlinx.serialization.encoding.Encoder, value: Any) {
        when (value) {
            is String -> encoder.encodeString(value)
            is Int -> encoder.encodeInt(value)
            is Long -> encoder.encodeLong(value)
            is Double -> encoder.encodeDouble(value)
            is Boolean -> encoder.encodeBoolean(value)
            is List<*> -> {
                val output = encoder.beginCollection(descriptor, value.size)
                value.forEachIndexed { index, item ->
                    output.encodeSerializableElement(descriptor, index, this, item ?: "null")
                }
                output.endStructure(descriptor)
            }
            is Map<*, *> -> {
                val output = encoder.beginCollection(descriptor, value.size)
                value.entries.forEachIndexed { index, entry ->
                    output.encodeSerializableElement(descriptor, index, this, entry.key ?: "null")
                    output.encodeSerializableElement(descriptor, index + 1, this, entry.value ?: "null")
                }
                output.endStructure(descriptor)
            }
            else -> encoder.encodeString(value.toString())
        }
    }
    
    override fun deserialize(decoder: kotlinx.serialization.encoding.Decoder): Any {
        return decoder.decodeString()
    }
} 