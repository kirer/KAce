package com.kace.content.infrastructure.persistence.repository

import com.kace.content.domain.model.ContentPermission
import com.kace.content.domain.model.ContentPermissionType
import com.kace.content.domain.model.ContentPermissionSubjectType
import com.kace.content.domain.repository.ContentPermissionRepository
import com.kace.content.infrastructure.persistence.entity.ContentPermissionEntity
import com.kace.content.infrastructure.persistence.entity.ContentPermissionTable
import com.kace.content.infrastructure.persistence.mapper.toContentPermission
import com.kace.content.infrastructure.persistence.mapper.toEntity
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.UUID

/**
 * 内容权限仓库实现
 */
class ContentPermissionRepositoryImpl(private val database: Database) : ContentPermissionRepository {
    /**
     * 创建权限
     */
    override suspend fun create(permission: ContentPermission): ContentPermission = newSuspendedTransaction(db = database) {
        val entity = permission.toEntity()
        entity.id
        permission.copy(id = entity.id.value)
    }

    /**
     * 删除权限
     */
    override suspend fun delete(id: UUID): Boolean = newSuspendedTransaction(db = database) {
        val entity = ContentPermissionEntity.findById(id) ?: return@newSuspendedTransaction false
        entity.delete()
        true
    }

    /**
     * 获取权限
     */
    override suspend fun getById(id: UUID): ContentPermission? = newSuspendedTransaction(db = database) {
        ContentPermissionEntity.findById(id)?.toContentPermission()
    }

    /**
     * 获取内容的所有权限
     */
    override suspend fun getByContent(contentId: UUID): List<ContentPermission> = newSuspendedTransaction(db = database) {
        ContentPermissionEntity.find {
            ContentPermissionTable.contentId eq contentId
        }.map { it.toContentPermission() }
    }

    /**
     * 获取内容类型的所有权限
     */
    override suspend fun getByContentType(contentTypeId: UUID): List<ContentPermission> = newSuspendedTransaction(db = database) {
        ContentPermissionEntity.find {
            ContentPermissionTable.contentTypeId eq contentTypeId
        }.map { it.toContentPermission() }
    }

    /**
     * 获取主体的所有权限
     */
    override suspend fun getBySubject(
        subjectType: ContentPermissionSubjectType,
        subjectId: UUID?
    ): List<ContentPermission> = newSuspendedTransaction(db = database) {
        val query = if (subjectId != null) {
            ContentPermissionEntity.find {
                (ContentPermissionTable.subjectType eq subjectType.name) and
                        (ContentPermissionTable.subjectId eq subjectId)
            }
        } else {
            ContentPermissionEntity.find {
                ContentPermissionTable.subjectType eq subjectType.name
            }
        }
        
        query.map { it.toContentPermission() }
    }

    /**
     * 获取内容的特定权限
     */
    override suspend fun getByContentAndPermissionType(
        contentId: UUID,
        permissionType: ContentPermissionType
    ): List<ContentPermission> = newSuspendedTransaction(db = database) {
        ContentPermissionEntity.find {
            (ContentPermissionTable.contentId eq contentId) and
                    (ContentPermissionTable.permissionType eq permissionType.name)
        }.map { it.toContentPermission() }
    }

    /**
     * 获取内容类型的特定权限
     */
    override suspend fun getByContentTypeAndPermissionType(
        contentTypeId: UUID,
        permissionType: ContentPermissionType
    ): List<ContentPermission> = newSuspendedTransaction(db = database) {
        ContentPermissionEntity.find {
            (ContentPermissionTable.contentTypeId eq contentTypeId) and
                    (ContentPermissionTable.permissionType eq permissionType.name)
        }.map { it.toContentPermission() }
    }

    /**
     * 检查主体是否有内容的特定权限
     */
    override suspend fun hasPermission(
        contentId: UUID,
        permissionType: ContentPermissionType,
        subjectType: ContentPermissionSubjectType,
        subjectId: UUID?
    ): Boolean = newSuspendedTransaction(db = database) {
        val query = if (subjectId != null) {
            ContentPermissionEntity.find {
                (ContentPermissionTable.contentId eq contentId) and
                        (ContentPermissionTable.permissionType eq permissionType.name) and
                        (ContentPermissionTable.subjectType eq subjectType.name) and
                        (ContentPermissionTable.subjectId eq subjectId)
            }
        } else {
            ContentPermissionEntity.find {
                (ContentPermissionTable.contentId eq contentId) and
                        (ContentPermissionTable.permissionType eq permissionType.name) and
                        (ContentPermissionTable.subjectType eq subjectType.name)
            }
        }
        
        query.count() > 0
    }

    /**
     * 检查主体是否有内容类型的特定权限
     */
    override suspend fun hasContentTypePermission(
        contentTypeId: UUID,
        permissionType: ContentPermissionType,
        subjectType: ContentPermissionSubjectType,
        subjectId: UUID?
    ): Boolean = newSuspendedTransaction(db = database) {
        val query = if (subjectId != null) {
            ContentPermissionEntity.find {
                (ContentPermissionTable.contentTypeId eq contentTypeId) and
                        (ContentPermissionTable.permissionType eq permissionType.name) and
                        (ContentPermissionTable.subjectType eq subjectType.name) and
                        (ContentPermissionTable.subjectId eq subjectId)
            }
        } else {
            ContentPermissionEntity.find {
                (ContentPermissionTable.contentTypeId eq contentTypeId) and
                        (ContentPermissionTable.permissionType eq permissionType.name) and
                        (ContentPermissionTable.subjectType eq subjectType.name)
            }
        }
        
        query.count() > 0
    }

    /**
     * 删除内容的所有权限
     */
    override suspend fun deleteByContent(contentId: UUID): Boolean = newSuspendedTransaction(db = database) {
        val entities = ContentPermissionEntity.find {
            ContentPermissionTable.contentId eq contentId
        }
        
        val count = entities.count()
        entities.forEach { it.delete() }
        
        count > 0
    }

    /**
     * 删除内容类型的所有权限
     */
    override suspend fun deleteByContentType(contentTypeId: UUID): Boolean = newSuspendedTransaction(db = database) {
        val entities = ContentPermissionEntity.find {
            ContentPermissionTable.contentTypeId eq contentTypeId
        }
        
        val count = entities.count()
        entities.forEach { it.delete() }
        
        count > 0
    }

    /**
     * 删除主体的所有权限
     */
    override suspend fun deleteBySubject(
        subjectType: ContentPermissionSubjectType,
        subjectId: UUID?
    ): Boolean = newSuspendedTransaction(db = database) {
        val query = if (subjectId != null) {
            ContentPermissionEntity.find {
                (ContentPermissionTable.subjectType eq subjectType.name) and
                        (ContentPermissionTable.subjectId eq subjectId)
            }
        } else {
            ContentPermissionEntity.find {
                ContentPermissionTable.subjectType eq subjectType.name
            }
        }
        
        val count = query.count()
        query.forEach { it.delete() }
        
        count > 0
    }
} 