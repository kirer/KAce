package com.kace.user.infrastructure.persistence.entity

import com.kace.user.domain.model.Role
import java.time.Instant
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.timestamp
import java.util.UUID

/**
 * 角色表定义
 */
object RoleTable : UUIDTable("roles") {
    val name = varchar("name", 50)
    val description = text("description").nullable()
    val permissions = text("permissions")  // 存储JSON格式的权限ID列表
    val isSystem = bool("is_system").default(false)
    val organizationId = uuid("organization_id").nullable()
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
    
    init {
        // 组织内角色名称唯一索引
        uniqueIndex(name, organizationId)
    }
}

/**
 * 角色实体
 */
class RoleEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<RoleEntity>(RoleTable)
    
    var name by RoleTable.name
    var description by RoleTable.description
    var permissions by RoleTable.permissions
    var isSystem by RoleTable.isSystem
    var organizationId by RoleTable.organizationId
    var createdAt by RoleTable.createdAt
    var updatedAt by RoleTable.updatedAt
    
    /**
     * 将实体转换为领域模型
     */
    fun toModel(): Role {
        val permissionsList = try {
            Json.decodeFromString<List<String>>(permissions)
        } catch (e: Exception) {
            emptyList()
        }
        
        return Role(
            id = id.value.toString(),
            name = name,
            description = description,
            permissions = permissionsList,
            isSystem = isSystem,
            organizationId = organizationId?.toString(),
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
    
    /**
     * 更新实体
     */
    fun update(role: Role) {
        name = role.name
        description = role.description
        permissions = Json.encodeToString(role.permissions)
        updatedAt = Instant.now()
    }
} 