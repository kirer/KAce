package com.kace.user.infrastructure.persistence.entity

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant
import java.util.*

/**
 * 组织表定义
 */
object Organizations : UUIDTable("organizations") {
    val name = varchar("name", 100)
    val description = text("description").nullable()
    val parentId = reference("parent_id", Organizations, onDelete = ReferenceOption.CASCADE).nullable()
    val createdAt = timestamp("created_at").default(Instant.now())
    val updatedAt = timestamp("updated_at").default(Instant.now())
}

/**
 * 用户组织关联表
 */
object UserOrganizations : Table("user_organizations") {
    val userId = reference("user_id", Users, onDelete = ReferenceOption.CASCADE)
    val organizationId = reference("organization_id", Organizations, onDelete = ReferenceOption.CASCADE)
    val role = varchar("role", 50)
    val createdAt = timestamp("created_at").default(Instant.now())
    
    override val primaryKey = PrimaryKey(userId, organizationId)
}

/**
 * 组织实体类
 */
class OrganizationEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<OrganizationEntity>(Organizations)
    
    var name by Organizations.name
    var description by Organizations.description
    var parentId by Organizations.parentId
    var createdAt by Organizations.createdAt
    var updatedAt by Organizations.updatedAt
    
    // 父组织关联
    var parent by OrganizationEntity optionalReferencedOn Organizations.parentId
    
    // 子组织关联
    val children by OrganizationEntity referrersOn Organizations.parentId
    
    // 用户关联（通过中间表）
    val userOrganizations by UserOrganizationEntity referrersOn UserOrganizations.organizationId
}

/**
 * 用户组织关联实体类
 */
class UserOrganizationEntity(userId: EntityID<UUID>, organizationId: EntityID<UUID>) {
    companion object : org.jetbrains.exposed.dao.id.CompositeEntityClass<UserOrganizationEntity, org.jetbrains.exposed.dao.id.EntityID<*>>(UserOrganizations)
    
    var userId by UserOrganizations.userId
    var organizationId by UserOrganizations.organizationId
    var role by UserOrganizations.role
    var createdAt by UserOrganizations.createdAt
    
    // 用户关联
    var user by UserEntity referencedOn UserOrganizations.userId
    
    // 组织关联
    var organization by OrganizationEntity referencedOn UserOrganizations.organizationId
} 