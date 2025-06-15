package com.kace.media.infrastructure.persistence.entity

import com.kace.media.domain.model.MediaStatus
import com.kace.media.domain.model.MediaType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp
import java.util.UUID

/**
 * 媒体表
 */
object MediaTable : UUIDTable("media") {
    val name = varchar("name", 255)
    val description = text("description").nullable()
    val type = enumerationByName("type", 20, MediaType::class)
    val mimeType = varchar("mime_type", 100)
    val size = long("size")
    val path = varchar("path", 500)
    val url = varchar("url", 500)
    val thumbnailUrl = varchar("thumbnail_url", 500).nullable()
    val width = integer("width").nullable()
    val height = integer("height").nullable()
    val duration = long("duration").nullable()
    val status = enumerationByName("status", 20, MediaStatus::class)
    val metadata = text("metadata").nullable()
    val folderId = uuid("folder_id").references(MediaFolderTable.id, onDelete = ReferenceOption.SET_NULL).nullable()
    val createdBy = uuid("created_by")
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
}

/**
 * 媒体文件夹表
 */
object MediaFolderTable : UUIDTable("media_folder") {
    val name = varchar("name", 255)
    val description = text("description").nullable()
    val parentId = uuid("parent_id").references(id, onDelete = ReferenceOption.CASCADE).nullable()
    val path = varchar("path", 500)
    val createdBy = uuid("created_by")
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
}

/**
 * 媒体标签表
 */
object MediaTagTable : UUIDTable("media_tag") {
    val name = varchar("name", 100).uniqueIndex()
    val slug = varchar("slug", 100).uniqueIndex()
    val createdBy = uuid("created_by")
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
}

/**
 * 媒体标签关联表
 */
object MediaTagMappingTable : Table("media_tag_mapping") {
    val mediaId = uuid("media_id").references(MediaTable.id, onDelete = ReferenceOption.CASCADE)
    val tagId = uuid("tag_id").references(MediaTagTable.id, onDelete = ReferenceOption.CASCADE)
    
    override val primaryKey = PrimaryKey(mediaId, tagId)
}

/**
 * 媒体处理任务表
 */
object MediaProcessingTaskTable : UUIDTable("media_processing_task") {
    val mediaId = uuid("media_id").references(MediaTable.id, onDelete = ReferenceOption.CASCADE)
    val type = varchar("type", 50)
    val status = varchar("status", 20)
    val parameters = text("parameters").nullable()
    val result = text("result").nullable()
    val errorMessage = text("error_message").nullable()
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
    val completedAt = timestamp("completed_at").nullable()
}

/**
 * 将Map转换为JSON字符串
 */
fun Map<String, Any>?.toJsonString(): String? {
    return this?.let { Json.encodeToString(it) }
}

/**
 * 从JSON字符串解析Map
 */
inline fun <reified T> String?.fromJsonString(): T? {
    return this?.let { Json.decodeFromString<T>(it) }
} 