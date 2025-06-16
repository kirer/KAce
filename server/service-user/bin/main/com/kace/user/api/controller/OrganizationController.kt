package com.kace.user.api.controller

import com.kace.common.exception.BadRequestException
import com.kace.common.exception.NotFoundException
import com.kace.common.model.dto.PageDto
import com.kace.common.model.dto.ResponseDto
import com.kace.user.api.request.CreateOrganizationRequest
import com.kace.user.api.request.UpdateOrganizationRequest
import com.kace.user.api.response.OrganizationResponse
import com.kace.user.domain.model.Organization
import com.kace.user.domain.service.OrganizationService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.Clock
import org.koin.ktor.ext.inject
import java.util.*

/**
 * 组织控制器
 */
fun Route.organizationController() {
    val organizationService by inject<OrganizationService>()
    
    route("/organizations") {
        // 获取组织列表
        get {
            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
            val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 20
            val query = call.request.queryParameters["query"]
            
            val orgsPage = organizationService.findAll(page, size, query)
            val responseDto = PageDto(
                content = orgsPage.content.map { it.toResponse() },
                pageNumber = orgsPage.pageNumber,
                pageSize = orgsPage.pageSize,
                totalElements = orgsPage.totalElements,
                totalPages = orgsPage.totalPages
            )
            
            call.respond(HttpStatusCode.OK, ResponseDto.success(responseDto))
        }
        
        // 创建组织
        post {
            val request = call.receive<CreateOrganizationRequest>()
            
            // 验证请求
            request.validate()
            
            val organization = Organization(
                id = UUID.randomUUID().toString(),
                name = request.name,
                description = request.description,
                parentId = request.parentId,
                createdAt = Clock.System.now(),
                updatedAt = Clock.System.now()
            )
            
            val createdOrg = organizationService.createOrganization(organization)
            call.respond(HttpStatusCode.Created, ResponseDto.success(createdOrg.toResponse()))
        }
        
        // 根据ID获取组织
        get("/{id}") {
            val id = call.parameters["id"] ?: throw BadRequestException("组织ID不能为空")
            
            val org = organizationService.findById(id) ?: throw NotFoundException("组织不存在")
            call.respond(HttpStatusCode.OK, ResponseDto.success(org.toResponse()))
        }
        
        // 更新组织
        put("/{id}") {
            val id = call.parameters["id"] ?: throw BadRequestException("组织ID不能为空")
            val request = call.receive<UpdateOrganizationRequest>()
            
            // 验证请求
            request.validate()
            
            val org = organizationService.findById(id) ?: throw NotFoundException("组织不存在")
            
            val updatedOrg = org.copy(
                name = request.name ?: org.name,
                description = request.description ?: org.description,
                parentId = request.parentId ?: org.parentId,
                updatedAt = Clock.System.now()
            )
            
            val result = organizationService.updateOrganization(updatedOrg)
            call.respond(HttpStatusCode.OK, ResponseDto.success(result.toResponse()))
        }
        
        // 删除组织
        delete("/{id}") {
            val id = call.parameters["id"] ?: throw BadRequestException("组织ID不能为空")
            
            val success = organizationService.deleteOrganization(id)
            if (success) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                throw NotFoundException("组织不存在")
            }
        }
        
        // 获取子组织
        get("/{id}/children") {
            val id = call.parameters["id"] ?: throw BadRequestException("组织ID不能为空")
            
            val children = organizationService.findChildrenById(id)
            call.respond(HttpStatusCode.OK, ResponseDto.success(children.map { it.toResponse() }))
        }
        
        // 获取组织成员
        get("/{id}/members") {
            val id = call.parameters["id"] ?: throw BadRequestException("组织ID不能为空")
            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
            val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 20
            
            val members = organizationService.getOrganizationMembers(id, page, size)
            call.respond(HttpStatusCode.OK, ResponseDto.success(members))
        }
        
        // 添加组织成员
        post("/{id}/members") {
            val id = call.parameters["id"] ?: throw BadRequestException("组织ID不能为空")
            val request = call.receive<Map<String, String>>()
            
            val userId = request["userId"] ?: throw BadRequestException("用户ID不能为空")
            val role = request["role"] ?: throw BadRequestException("角色不能为空")
            
            val success = organizationService.addMember(id, userId, role)
            if (success) {
                call.respond(HttpStatusCode.OK, ResponseDto.success(null))
            } else {
                throw BadRequestException("添加成员失败")
            }
        }
        
        // 更新组织成员角色
        put("/{id}/members/{userId}") {
            val id = call.parameters["id"] ?: throw BadRequestException("组织ID不能为空")
            val userId = call.parameters["userId"] ?: throw BadRequestException("用户ID不能为空")
            val request = call.receive<Map<String, String>>()
            
            val role = request["role"] ?: throw BadRequestException("角色不能为空")
            
            val success = organizationService.updateMemberRole(id, userId, role)
            if (success) {
                call.respond(HttpStatusCode.OK, ResponseDto.success(null))
            } else {
                throw NotFoundException("组织或成员不存在")
            }
        }
        
        // 移除组织成员
        delete("/{id}/members/{userId}") {
            val id = call.parameters["id"] ?: throw BadRequestException("组织ID不能为空")
            val userId = call.parameters["userId"] ?: throw BadRequestException("用户ID不能为空")
            
            val success = organizationService.removeMember(id, userId)
            if (success) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                throw NotFoundException("组织或成员不存在")
            }
        }
    }
}

/**
 * 将组织模型转换为响应对象
 */
private fun Organization.toResponse(): OrganizationResponse {
    return OrganizationResponse(
        id = id,
        name = name,
        description = description,
        parentId = parentId,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

/**
 * 验证创建组织请求
 */
private fun CreateOrganizationRequest.validate() {
    if (name.isBlank()) throw BadRequestException("组织名称不能为空")
}

/**
 * 验证更新组织请求
 */
private fun UpdateOrganizationRequest.validate() {
    name?.let { if (it.isBlank()) throw BadRequestException("组织名称不能为空") }
} 