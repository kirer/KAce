package com.kace.media.api.controller

import com.kace.common.exception.BadRequestException
import com.kace.common.exception.NotFoundException
import com.kace.common.response.ApiResponse
import com.kace.common.response.ErrorCode
import com.kace.media.api.request.*
import com.kace.media.api.response.MediaPermissionResponse
import com.kace.media.api.response.PermissionCheckResponse
import com.kace.media.api.response.toResponse
import com.kace.media.domain.model.MediaPermission
import com.kace.media.domain.model.MediaPermissionType
import com.kace.media.domain.model.PermissionGranteeType
import com.kace.media.domain.service.MediaPermissionService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import java.time.Instant
import java.util.*

/**
 * 媒体权限控制器
 */
class MediaPermissionController(
    private val mediaPermissionService: MediaPermissionService
) {
    /**
     * 获取媒体文件的权限列表
     */
    suspend fun getMediaPermissions(call: ApplicationCall) {
        val mediaId = call.parameters["mediaId"] ?: throw BadRequestException("媒体ID不能为空")
        
        val permissions = mediaPermissionService.getMediaPermissions(UUID.fromString(mediaId))
        call.respond(HttpStatusCode.OK, ApiResponse.success(permissions.toResponse()))
    }
    
    /**
     * 获取文件夹的权限列表
     */
    suspend fun getFolderPermissions(call: ApplicationCall) {
        val folderId = call.parameters["folderId"] ?: throw BadRequestException("文件夹ID不能为空")
        
        val permissions = mediaPermissionService.getFolderPermissions(UUID.fromString(folderId))
        call.respond(HttpStatusCode.OK, ApiResponse.success(permissions.toResponse()))
    }
    
    /**
     * 创建媒体权限
     */
    suspend fun createPermission(call: ApplicationCall) {
        val request = call.receive<CreateMediaPermissionRequest>()
        
        if (request.mediaId == null && request.folderId == null) {
            throw BadRequestException("媒体ID或文件夹ID至少需要提供一个")
        }
        
        if (request.mediaId != null && request.folderId != null) {
            throw BadRequestException("媒体ID和文件夹ID不能同时提供")
        }
        
        val permissionType = try {
            MediaPermissionType.valueOf(request.permissionType)
        } catch (e: IllegalArgumentException) {
            throw BadRequestException("无效的权限类型: ${request.permissionType}")
        }
        
        val granteeType = try {
            PermissionGranteeType.valueOf(request.granteeType)
        } catch (e: IllegalArgumentException) {
            throw BadRequestException("无效的授权对象类型: ${request.granteeType}")
        }
        
        val granteeId = if (granteeType != PermissionGranteeType.PUBLIC && request.granteeId == null) {
            throw BadRequestException("非公开权限必须提供授权对象ID")
        } else if (granteeType == PermissionGranteeType.PUBLIC && request.granteeId != null) {
            throw BadRequestException("公开权限不应提供授权对象ID")
        } else {
            request.granteeId?.let { UUID.fromString(it) }
        }
        
        val now = Instant.now()
        val expiresAt = request.expiresAtEpochMillis?.let { Instant.ofEpochMilli(it) }
        
        val permission = MediaPermission(
            id = UUID.randomUUID(),
            mediaId = request.mediaId?.let { UUID.fromString(it) },
            folderId = request.folderId?.let { UUID.fromString(it) },
            permissionType = permissionType,
            granteeType = granteeType,
            granteeId = granteeId,
            isInherited = false,
            expiresAt = expiresAt,
            createdBy = call.principal<UserIdPrincipal>()?.userId?.let { UUID.fromString(it) }
                ?: UUID.fromString("00000000-0000-0000-0000-000000000000"),  // 默认系统用户ID
            createdAt = now,
            updatedAt = now
        )
        
        val savedPermission = mediaPermissionService.createPermission(permission)
        call.respond(HttpStatusCode.Created, ApiResponse.success(savedPermission.toResponse()))
    }
    
    /**
     * 删除权限
     */
    suspend fun deletePermission(call: ApplicationCall) {
        val id = call.parameters["id"] ?: throw BadRequestException("权限ID不能为空")
        
        val result = mediaPermissionService.deletePermission(UUID.fromString(id))
        if (result) {
            call.respond(HttpStatusCode.NoContent, ApiResponse.success(null))
        } else {
            throw NotFoundException("权限不存在或已删除")
        }
    }
    
    /**
     * 授予用户媒体权限
     */
    suspend fun grantUserMediaPermission(call: ApplicationCall) {
        val mediaId = call.parameters["mediaId"] ?: throw BadRequestException("媒体ID不能为空")
        val request = call.receive<GrantUserPermissionRequest>()
        
        val permissionType = try {
            MediaPermissionType.valueOf(request.permissionType)
        } catch (e: IllegalArgumentException) {
            throw BadRequestException("无效的权限类型: ${request.permissionType}")
        }
        
        val permission = mediaPermissionService.grantUserMediaPermission(
            mediaId = UUID.fromString(mediaId),
            userId = UUID.fromString(request.userId),
            permissionType = permissionType,
            expiresAt = request.expiresAtEpochMillis
        )
        
        call.respond(HttpStatusCode.Created, ApiResponse.success(permission.toResponse()))
    }
    
    /**
     * 授予用户文件夹权限
     */
    suspend fun grantUserFolderPermission(call: ApplicationCall) {
        val folderId = call.parameters["folderId"] ?: throw BadRequestException("文件夹ID不能为空")
        val request = call.receive<GrantUserPermissionRequest>()
        val applyToChildren = call.request.queryParameters["applyToChildren"]?.toBoolean() ?: false
        
        val permissionType = try {
            MediaPermissionType.valueOf(request.permissionType)
        } catch (e: IllegalArgumentException) {
            throw BadRequestException("无效的权限类型: ${request.permissionType}")
        }
        
        val permission = mediaPermissionService.grantUserFolderPermission(
            folderId = UUID.fromString(folderId),
            userId = UUID.fromString(request.userId),
            permissionType = permissionType,
            expiresAt = request.expiresAtEpochMillis,
            applyToChildren = applyToChildren
        )
        
        call.respond(HttpStatusCode.Created, ApiResponse.success(permission.toResponse()))
    }
    
    /**
     * 授予角色媒体权限
     */
    suspend fun grantRoleMediaPermission(call: ApplicationCall) {
        val mediaId = call.parameters["mediaId"] ?: throw BadRequestException("媒体ID不能为空")
        val request = call.receive<GrantRolePermissionRequest>()
        
        val permissionType = try {
            MediaPermissionType.valueOf(request.permissionType)
        } catch (e: IllegalArgumentException) {
            throw BadRequestException("无效的权限类型: ${request.permissionType}")
        }
        
        val permission = mediaPermissionService.grantRoleMediaPermission(
            mediaId = UUID.fromString(mediaId),
            roleId = UUID.fromString(request.roleId),
            permissionType = permissionType
        )
        
        call.respond(HttpStatusCode.Created, ApiResponse.success(permission.toResponse()))
    }
    
    /**
     * 授予角色文件夹权限
     */
    suspend fun grantRoleFolderPermission(call: ApplicationCall) {
        val folderId = call.parameters["folderId"] ?: throw BadRequestException("文件夹ID不能为空")
        val request = call.receive<GrantRolePermissionRequest>()
        val applyToChildren = call.request.queryParameters["applyToChildren"]?.toBoolean() ?: false
        
        val permissionType = try {
            MediaPermissionType.valueOf(request.permissionType)
        } catch (e: IllegalArgumentException) {
            throw BadRequestException("无效的权限类型: ${request.permissionType}")
        }
        
        val permission = mediaPermissionService.grantRoleFolderPermission(
            folderId = UUID.fromString(folderId),
            roleId = UUID.fromString(request.roleId),
            permissionType = permissionType,
            applyToChildren = applyToChildren
        )
        
        call.respond(HttpStatusCode.Created, ApiResponse.success(permission.toResponse()))
    }
    
    /**
     * 授予组织媒体权限
     */
    suspend fun grantOrganizationMediaPermission(call: ApplicationCall) {
        val mediaId = call.parameters["mediaId"] ?: throw BadRequestException("媒体ID不能为空")
        val request = call.receive<GrantOrganizationPermissionRequest>()
        
        val permissionType = try {
            MediaPermissionType.valueOf(request.permissionType)
        } catch (e: IllegalArgumentException) {
            throw BadRequestException("无效的权限类型: ${request.permissionType}")
        }
        
        val permission = mediaPermissionService.grantOrganizationMediaPermission(
            mediaId = UUID.fromString(mediaId),
            organizationId = UUID.fromString(request.organizationId),
            permissionType = permissionType
        )
        
        call.respond(HttpStatusCode.Created, ApiResponse.success(permission.toResponse()))
    }
    
    /**
     * 授予组织文件夹权限
     */
    suspend fun grantOrganizationFolderPermission(call: ApplicationCall) {
        val folderId = call.parameters["folderId"] ?: throw BadRequestException("文件夹ID不能为空")
        val request = call.receive<GrantOrganizationPermissionRequest>()
        val applyToChildren = call.request.queryParameters["applyToChildren"]?.toBoolean() ?: false
        
        val permissionType = try {
            MediaPermissionType.valueOf(request.permissionType)
        } catch (e: IllegalArgumentException) {
            throw BadRequestException("无效的权限类型: ${request.permissionType}")
        }
        
        val permission = mediaPermissionService.grantOrganizationFolderPermission(
            folderId = UUID.fromString(folderId),
            organizationId = UUID.fromString(request.organizationId),
            permissionType = permissionType,
            applyToChildren = applyToChildren
        )
        
        call.respond(HttpStatusCode.Created, ApiResponse.success(permission.toResponse()))
    }
    
    /**
     * 设置媒体为公开访问
     */
    suspend fun makeMediaPublic(call: ApplicationCall) {
        val mediaId = call.parameters["mediaId"] ?: throw BadRequestException("媒体ID不能为空")
        val request = call.receive<MakePublicRequest>()
        
        val permissionTypes = request.permissionTypes.map {
            try {
                MediaPermissionType.valueOf(it)
            } catch (e: IllegalArgumentException) {
                throw BadRequestException("无效的权限类型: $it")
            }
        }.toSet()
        
        val permissions = mediaPermissionService.makeMediaPublic(
            mediaId = UUID.fromString(mediaId),
            permissionTypes = permissionTypes
        )
        
        call.respond(HttpStatusCode.Created, ApiResponse.success(permissions.toResponse()))
    }
    
    /**
     * 设置文件夹为公开访问
     */
    suspend fun makeFolderPublic(call: ApplicationCall) {
        val folderId = call.parameters["folderId"] ?: throw BadRequestException("文件夹ID不能为空")
        val request = call.receive<MakePublicRequest>()
        
        val permissionTypes = request.permissionTypes.map {
            try {
                MediaPermissionType.valueOf(it)
            } catch (e: IllegalArgumentException) {
                throw BadRequestException("无效的权限类型: $it")
            }
        }.toSet()
        
        val permissions = mediaPermissionService.makeFolderPublic(
            folderId = UUID.fromString(folderId),
            permissionTypes = permissionTypes,
            applyToChildren = request.applyToChildren
        )
        
        call.respond(HttpStatusCode.Created, ApiResponse.success(permissions.toResponse()))
    }
    
    /**
     * 撤销媒体的公开访问
     */
    suspend fun makeMediaPrivate(call: ApplicationCall) {
        val mediaId = call.parameters["mediaId"] ?: throw BadRequestException("媒体ID不能为空")
        
        val result = mediaPermissionService.makeMediaPrivate(UUID.fromString(mediaId))
        
        if (result) {
            call.respond(HttpStatusCode.NoContent, ApiResponse.success(null))
        } else {
            call.respond(HttpStatusCode.OK, ApiResponse.fail(ErrorCode.OPERATION_FAILED, "无公开权限需要撤销"))
        }
    }
    
    /**
     * 撤销文件夹的公开访问
     */
    suspend fun makeFolderPrivate(call: ApplicationCall) {
        val folderId = call.parameters["folderId"] ?: throw BadRequestException("文件夹ID不能为空")
        val applyToChildren = call.request.queryParameters["applyToChildren"]?.toBoolean() ?: false
        
        val result = mediaPermissionService.makeFolderPrivate(
            folderId = UUID.fromString(folderId),
            applyToChildren = applyToChildren
        )
        
        if (result) {
            call.respond(HttpStatusCode.NoContent, ApiResponse.success(null))
        } else {
            call.respond(HttpStatusCode.OK, ApiResponse.fail(ErrorCode.OPERATION_FAILED, "无公开权限需要撤销"))
        }
    }
    
    /**
     * 检查用户是否有媒体的指定权限
     */
    suspend fun checkMediaPermission(call: ApplicationCall) {
        val mediaId = call.parameters["mediaId"] ?: throw BadRequestException("媒体ID不能为空")
        val request = call.receive<CheckPermissionRequest>()
        
        val permissionType = try {
            MediaPermissionType.valueOf(request.permissionType)
        } catch (e: IllegalArgumentException) {
            throw BadRequestException("无效的权限类型: ${request.permissionType}")
        }
        
        val result = mediaPermissionService.userHasMediaPermission(
            mediaId = UUID.fromString(mediaId),
            userId = UUID.fromString(request.userId),
            permissionType = permissionType
        )
        
        call.respond(HttpStatusCode.OK, ApiResponse.success(PermissionCheckResponse(result)))
    }
    
    /**
     * 检查用户是否有文件夹的指定权限
     */
    suspend fun checkFolderPermission(call: ApplicationCall) {
        val folderId = call.parameters["folderId"] ?: throw BadRequestException("文件夹ID不能为空")
        val request = call.receive<CheckPermissionRequest>()
        
        val permissionType = try {
            MediaPermissionType.valueOf(request.permissionType)
        } catch (e: IllegalArgumentException) {
            throw BadRequestException("无效的权限类型: ${request.permissionType}")
        }
        
        val result = mediaPermissionService.userHasFolderPermission(
            folderId = UUID.fromString(folderId),
            userId = UUID.fromString(request.userId),
            permissionType = permissionType
        )
        
        call.respond(HttpStatusCode.OK, ApiResponse.success(PermissionCheckResponse(result)))
    }
}

/**
 * 用户ID主体
 * 用于模拟当前认证用户
 */
data class UserIdPrincipal(val userId: String) : Principal 