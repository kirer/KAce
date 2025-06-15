package com.kace.user.domain.repository

import com.kace.common.model.dto.PageDto
import com.kace.user.domain.model.Organization
import com.kace.user.domain.model.OrganizationMember
import com.kace.user.domain.model.OrganizationRole
import com.kace.user.domain.model.OrganizationStatus

/**
 * 组织仓库接口
 */
interface OrganizationRepository {
    /**
     * 根据ID获取组织
     * @param id 组织ID
     * @return 组织对象，如果不存在则返回null
     */
    suspend fun getById(id: String): Organization?
    
    /**
     * 根据名称获取组织
     * @param name 组织名称
     * @return 组织对象，如果不存在则返回null
     */
    suspend fun getByName(name: String): Organization?
    
    /**
     * 分页获取组织列表
     * @param page 页码
     * @param size 每页大小
     * @param status 组织状态过滤
     * @param query 搜索关键词
     * @return 组织分页对象
     */
    suspend fun getOrganizations(page: Int, size: Int, status: OrganizationStatus? = null, query: String? = null): PageDto<Organization>
    
    /**
     * 创建组织
     * @param organization 组织对象
     * @return 创建的组织对象
     */
    suspend fun createOrganization(organization: Organization): Organization
    
    /**
     * 更新组织
     * @param organization 组织对象
     * @return 更新后的组织对象
     */
    suspend fun updateOrganization(organization: Organization): Organization
    
    /**
     * 更新组织状态
     * @param id 组织ID
     * @param status 组织状态
     * @return 是否更新成功
     */
    suspend fun updateStatus(id: String, status: OrganizationStatus): Boolean
    
    /**
     * 删除组织
     * @param id 组织ID
     * @return 是否删除成功
     */
    suspend fun deleteOrganization(id: String): Boolean
    
    /**
     * 获取组织成员
     * @param organizationId 组织ID
     * @param page 页码
     * @param size 每页大小
     * @return 组织成员分页对象
     */
    suspend fun getMembers(organizationId: String, page: Int, size: Int): PageDto<OrganizationMember>
    
    /**
     * 添加组织成员
     * @param member 组织成员对象
     * @return 添加的组织成员对象
     */
    suspend fun addMember(member: OrganizationMember): OrganizationMember
    
    /**
     * 更新组织成员角色
     * @param id 组织成员ID
     * @param role 组织角色
     * @return 是否更新成功
     */
    suspend fun updateMemberRole(id: String, role: OrganizationRole): Boolean
    
    /**
     * 移除组织成员
     * @param id 组织成员ID
     * @return 是否移除成功
     */
    suspend fun removeMember(id: String): Boolean
    
    /**
     * 获取用户所属的组织
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 组织分页对象
     */
    suspend fun getUserOrganizations(userId: String, page: Int, size: Int): PageDto<Organization>
} 