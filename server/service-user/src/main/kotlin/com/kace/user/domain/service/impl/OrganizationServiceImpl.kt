package com.kace.user.domain.service.impl

import com.kace.common.exception.BadRequestException
import com.kace.common.exception.NotFoundException
import com.kace.common.model.dto.PageDto
import com.kace.user.domain.model.Organization
import com.kace.user.domain.model.User
import com.kace.user.domain.repository.OrganizationRepository
import com.kace.user.domain.repository.UserRepository
import com.kace.user.domain.service.OrganizationService
import kotlinx.datetime.Clock
import org.slf4j.LoggerFactory
import java.util.*

/**
 * 组织服务实现类
 */
class OrganizationServiceImpl(
    private val organizationRepository: OrganizationRepository,
    private val userRepository: UserRepository
) : OrganizationService {
    
    private val logger = LoggerFactory.getLogger(OrganizationServiceImpl::class.java)
    
    /**
     * 获取组织列表
     */
    override suspend fun getOrganizations(page: Int, size: Int, query: String?): PageDto<Organization> {
        return organizationRepository.findAll(page, size, query)
    }
    
    /**
     * 获取组织
     */
    override suspend fun getOrganization(id: String): Organization? {
        return organizationRepository.findById(id)
    }
    
    /**
     * 创建组织
     */
    override suspend fun createOrganization(organization: Organization): Organization {
        // 生成组织ID
        val orgId = UUID.randomUUID().toString()
        
        // 创建组织
        val newOrg = organization.copy(
            id = orgId,
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now()
        )
        
        return organizationRepository.save(newOrg)
    }
    
    /**
     * 更新组织
     */
    override suspend fun updateOrganization(organization: Organization): Organization {
        // 检查组织是否存在
        if (organizationRepository.findById(organization.id) == null) {
            throw NotFoundException("组织不存在")
        }
        
        // 更新组织
        val updatedOrg = organization.copy(
            updatedAt = Clock.System.now()
        )
        
        return organizationRepository.save(updatedOrg)
    }
    
    /**
     * 删除组织
     */
    override suspend fun deleteOrganization(id: String): Boolean {
        // 检查组织是否存在
        if (organizationRepository.findById(id) == null) {
            throw NotFoundException("组织不存在")
        }
        
        // 检查是否有子组织
        if (organizationRepository.countByParentId(id) > 0) {
            throw BadRequestException("无法删除包含子组织的组织")
        }
        
        // 删除组织
        return organizationRepository.deleteById(id)
    }
    
    /**
     * 获取子组织
     */
    override suspend fun getChildOrganizations(parentId: String): List<Organization> {
        // 检查父组织是否存在
        if (organizationRepository.findById(parentId) == null) {
            throw NotFoundException("父组织不存在")
        }
        
        return organizationRepository.findByParentId(parentId)
    }
    
    /**
     * 添加用户到组织
     */
    override suspend fun addUserToOrganization(organizationId: String, userId: String, role: String): Boolean {
        // 检查组织是否存在
        if (organizationRepository.findById(organizationId) == null) {
            throw NotFoundException("组织不存在")
        }
        
        // 检查用户是否存在
        if (userRepository.findById(userId) == null) {
            throw NotFoundException("用户不存在")
        }
        
        // 检查用户是否已在组织中
        if (organizationRepository.isUserInOrganization(organizationId, userId)) {
            throw BadRequestException("用户已在组织中")
        }
        
        // 添加用户到组织
        return organizationRepository.addUserToOrganization(organizationId, userId, role)
    }
    
    /**
     * 从组织中移除用户
     */
    override suspend fun removeUserFromOrganization(organizationId: String, userId: String): Boolean {
        // 检查组织是否存在
        if (organizationRepository.findById(organizationId) == null) {
            throw NotFoundException("组织不存在")
        }
        
        // 检查用户是否存在
        if (userRepository.findById(userId) == null) {
            throw NotFoundException("用户不存在")
        }
        
        // 检查用户是否在组织中
        if (!organizationRepository.isUserInOrganization(organizationId, userId)) {
            throw BadRequestException("用户不在组织中")
        }
        
        // 从组织中移除用户
        return organizationRepository.removeUserFromOrganization(organizationId, userId)
    }
    
    /**
     * 更新用户在组织中的角色
     */
    override suspend fun updateUserOrganizationRole(organizationId: String, userId: String, role: String): Boolean {
        // 检查组织是否存在
        if (organizationRepository.findById(organizationId) == null) {
            throw NotFoundException("组织不存在")
        }
        
        // 检查用户是否存在
        if (userRepository.findById(userId) == null) {
            throw NotFoundException("用户不存在")
        }
        
        // 检查用户是否在组织中
        if (!organizationRepository.isUserInOrganization(organizationId, userId)) {
            throw BadRequestException("用户不在组织中")
        }
        
        // 更新用户角色
        return organizationRepository.updateUserOrganizationRole(organizationId, userId, role)
    }
    
    /**
     * 获取组织中的用户
     */
    override suspend fun getUsersInOrganization(organizationId: String, page: Int, size: Int): PageDto<User> {
        // 检查组织是否存在
        if (organizationRepository.findById(organizationId) == null) {
            throw NotFoundException("组织不存在")
        }
        
        return organizationRepository.findUsersInOrganization(organizationId, page, size)
    }
    
    /**
     * 获取用户所在的组织
     */
    override suspend fun getOrganizationsForUser(userId: String): List<Organization> {
        // 检查用户是否存在
        if (userRepository.findById(userId) == null) {
            throw NotFoundException("用户不存在")
        }
        
        return organizationRepository.findByUserId(userId)
    }
} 