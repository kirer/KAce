package com.kace.user.infrastructure.persistence.entity

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant
import java.util.*

/**
 * 用户资料表定义
 */
object UserProfiles : UUIDTable("user_profiles") {
    val userId = reference("user_id", Users, onDelete = ReferenceOption.CASCADE).uniqueIndex()
    val bio = text("bio").nullable()
    val phoneNumber = varchar("phone_number", 20).nullable()
    val birthDate = varchar("birth_date", 10).nullable() // 格式：YYYY-MM-DD
    val gender = varchar("gender", 20).nullable()
    val addressJson = text("address_json").nullable()
    val preferencesJson = text("preferences_json").nullable()
    val createdAt = timestamp("created_at").default(Instant.now())
    val updatedAt = timestamp("updated_at").default(Instant.now())
}

/**
 * 用户资料实体类
 */
class UserProfileEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<UserProfileEntity>(UserProfiles)
    
    var userId by UserProfiles.userId
    var bio by UserProfiles.bio
    var phoneNumber by UserProfiles.phoneNumber
    var birthDate by UserProfiles.birthDate
    var gender by UserProfiles.gender
    var addressJson by UserProfiles.addressJson
    var preferencesJson by UserProfiles.preferencesJson
    var createdAt by UserProfiles.createdAt
    var updatedAt by UserProfiles.updatedAt
    
    // 用户关联
    var user by UserEntity referencedOn UserProfiles.userId
} 