package com.kace.content.infrastructure.persistence.entity

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import java.util.UUID

/**
 * 内容权限表
 */
object ContentPermissionTable : UUIDTable("content_permissions") {
    /**
     * 内容ID
     */
    val contentId = uuid("content_id").nullable().index()
    
    /**
     * 内容类型ID
     */
    val contentTypeId = uuid("content_type_id").nullable().index()
    
    /**
     * 权限类型
     */
    val permissionType = varchar("permission_type", 50)
    
    /**
     * 主体类型
     */
    val subjectType = varchar("subject_type", 50)
    
    /**
     * 主体ID
     */
    val subjectId = uuid("subject_id").nullable()
    
    /**
     * 创建者ID
     */
    val createdBy = uuid("created_by")
    
    /**
     * 创建时间
     */
    val createdAt = long("created_at")
    
    /**
     * 更新时间
     */
    val updatedAt = long("updated_at")
    
    init {
        // 创建复合索引
        index(isUnique = false, contentId, permissionType, subjectType)
        index(isUnique = false, contentTypeId, permissionType, subjectType)
        index(isUnique = false, subjectType, subjectId)
        
        // 添加约束：内容ID和内容类型ID不能同时为空
        check("content_id_or_content_type_id_not_null") {
            contentId.isNotNull() or contentTypeId.isNotNull()
        }
        
        // 添加约束：如果主体类型不是PUBLIC，则主体ID不能为空
        check("subject_id_not_null_if_not_public") {
            (subjectType neq "PUBLIC") and subjectId.isNotNull() or
                    (subjectType eq "PUBLIC")
        }
        
        // 添加唯一约束：内容ID + 权限类型 + 主体类型 + 主体ID
        uniqueIndex(contentId, permissionType, subjectType, subjectId)
        
        // 添加唯一约束：内容类型ID + 权限类型 + 主体类型 + 主体ID
        uniqueIndex(contentTypeId, permissionType, subjectType, subjectId)
    }
}

/**
 * 内容权限实体
 */
class ContentPermissionEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ContentPermissionEntity>(ContentPermissionTable)
    
    var contentId by ContentPermissionTable.contentId
    var contentTypeId by ContentPermissionTable.contentTypeId
    var permissionType by ContentPermissionTable.permissionType
    var subjectType by ContentPermissionTable.subjectType
    var subjectId by ContentPermissionTable.subjectId
    var createdBy by ContentPermissionTable.createdBy
    var createdAt by ContentPermissionTable.createdAt
    var updatedAt by ContentPermissionTable.updatedAt
} 