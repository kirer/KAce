package com.kace.system.api.controller

import com.kace.system.api.request.BatchUpdateSystemConfigsRequest
import com.kace.system.api.request.CreateSystemConfigRequest
import com.kace.system.api.request.UpdateSystemConfigRequest
import com.kace.system.api.response.SystemConfigResponse
import com.kace.system.api.response.groupByCategory
import com.kace.system.api.response.toResponse
import com.kace.system.domain.model.SystemConfig
import com.kace.system.domain.service.SystemConfigService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory

/**
 * 系统配置控制器
 *
 * @property configService 系统配置服务
 */
class SystemConfigController(private val configService: SystemConfigService) {
    private val logger = LoggerFactory.getLogger(SystemConfigController::class.java)
    
    /**
     * 注册路由
     */
    fun registerRoutes(route: Route) {
        route.route("/configs") {
            // 获取所有系统配置
            get {
                logger.debug("获取所有系统配置")
                val configs = configService.getAllConfigs()
                call.respond(configs.map { it.toResponse() })
            }
            
            // 按分类分组获取系统配置
            get("/grouped") {
                logger.debug("获取分组的系统配置")
                val configs = configService.getAllConfigs()
                call.respond(configs.groupByCategory())
            }
            
            // 根据键获取系统配置
            get("/{key}") {
                val key = call.parameters["key"] ?: run {
                    call.respond(HttpStatusCode.BadRequest, "缺少参数: key")
                    return@get
                }
                
                logger.debug("根据键获取系统配置: $key")
                val config = configService.getConfigByKey(key)
                
                if (config != null) {
                    call.respond(config.toResponse())
                } else {
                    call.respond(HttpStatusCode.NotFound, "未找到配置: $key")
                }
            }
            
            // 根据分类获取系统配置
            get("/category/{category}") {
                val category = call.parameters["category"] ?: run {
                    call.respond(HttpStatusCode.BadRequest, "缺少参数: category")
                    return@get
                }
                
                logger.debug("根据分类获取系统配置: $category")
                val configs = configService.getConfigsByCategory(category)
                call.respond(configs.map { it.toResponse() })
            }
            
            // 创建系统配置
            post {
                try {
                    val request = call.receive<CreateSystemConfigRequest>()
                    logger.info("创建系统配置: ${request.key}")
                    
                    val config = SystemConfig(
                        key = request.key,
                        value = request.value,
                        type = request.type,
                        description = request.description,
                        category = request.category,
                        editable = request.editable
                    )
                    
                    val createdConfig = configService.createConfig(config)
                    call.respond(HttpStatusCode.Created, createdConfig.toResponse())
                } catch (e: Exception) {
                    logger.error("创建系统配置失败", e)
                    call.respond(
                        HttpStatusCode.BadRequest, 
                        mapOf("error" to (e.message ?: "创建系统配置失败"))
                    )
                }
            }
            
            // 更新系统配置
            put("/{key}") {
                val key = call.parameters["key"] ?: run {
                    call.respond(HttpStatusCode.BadRequest, "缺少参数: key")
                    return@put
                }
                
                try {
                    val request = call.receive<UpdateSystemConfigRequest>()
                    logger.info("更新系统配置: $key")
                    
                    val updatedConfig = configService.updateConfig(key, request.value)
                    
                    if (updatedConfig != null) {
                        call.respond(updatedConfig.toResponse())
                    } else {
                        call.respond(HttpStatusCode.NotFound, "未找到配置: $key")
                    }
                } catch (e: Exception) {
                    logger.error("更新系统配置失败: $key", e)
                    call.respond(
                        HttpStatusCode.BadRequest, 
                        mapOf("error" to (e.message ?: "更新系统配置失败"))
                    )
                }
            }
            
            // 批量更新系统配置
            put {
                try {
                    val request = call.receive<BatchUpdateSystemConfigsRequest>()
                    logger.info("批量更新系统配置, 数量: ${request.configs.size}")
                    
                    // 获取现有配置
                    val existingConfigMap = configService.getAllConfigs().associateBy { it.key }
                    
                    // 准备更新的配置列表
                    val configsToUpdate = request.configs.mapNotNull { item ->
                        existingConfigMap[item.key]?.copy(value = item.value)
                    }
                    
                    if (configsToUpdate.isEmpty()) {
                        call.respond(HttpStatusCode.BadRequest, "没有有效的配置可更新")
                        return@put
                    }
                    
                    val updatedConfigs = configService.batchUpsertConfigs(configsToUpdate)
                    call.respond(updatedConfigs.map { it.toResponse() })
                } catch (e: Exception) {
                    logger.error("批量更新系统配置失败", e)
                    call.respond(
                        HttpStatusCode.BadRequest, 
                        mapOf("error" to (e.message ?: "批量更新系统配置失败"))
                    )
                }
            }
            
            // 删除系统配置
            delete("/{key}") {
                val key = call.parameters["key"] ?: run {
                    call.respond(HttpStatusCode.BadRequest, "缺少参数: key")
                    return@delete
                }
                
                try {
                    logger.info("删除系统配置: $key")
                    val deleted = configService.deleteConfig(key)
                    
                    if (deleted) {
                        call.respond(HttpStatusCode.NoContent)
                    } else {
                        call.respond(HttpStatusCode.NotFound, "未找到配置: $key")
                    }
                } catch (e: Exception) {
                    logger.error("删除系统配置失败: $key", e)
                    call.respond(
                        HttpStatusCode.BadRequest, 
                        mapOf("error" to (e.message ?: "删除系统配置失败"))
                    )
                }
            }
            
            // 获取可编辑的系统配置
            get("/editable") {
                logger.debug("获取可编辑的系统配置")
                val configs = configService.getEditableConfigs()
                call.respond(configs.map { it.toResponse() })
            }
        }
    }
} 