package com.kace.user.api.controller

import com.kace.common.model.dto.ApiResponse
import com.kace.user.api.request.ImportPreferencesRequest
import com.kace.user.api.request.SetPreferenceRequest
import com.kace.user.api.request.SetPreferencesRequest
import com.kace.user.api.response.ExportPreferencesResponse
import com.kace.user.api.response.UserPreferenceResponse
import com.kace.user.domain.model.UserPreference
import com.kace.user.domain.service.UserPreferenceService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.slf4j.LoggerFactory
import java.util.UUID

/**
 * 用户偏好设置控制器路由
 */
fun Route.userPreferenceRoutes(preferenceService: UserPreferenceService) {
    val logger = LoggerFactory.getLogger("UserPreferenceController")
    
    // 需要认证的路由
    authenticate("jwt") {
        route("/api/v1/user/preferences") {
            /**
             * 获取所有偏好设置
             */
            get {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.subject
                    
                    if (userId != null) {
                        val preferences = preferenceService.getAllPreferences(UUID.fromString(userId))
                        val response = preferences.map { UserPreferenceResponse.fromDomain(it) }
                        call.respond(ApiResponse.success(response))
                    } else {
                        call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("Unauthorized"))
                    }
                } catch (e: Exception) {
                    logger.error("Get preferences error", e)
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse.error("Failed to get preferences: ${e.message}"))
                }
            }
            
            /**
             * 按类别获取偏好设置
             */
            get("/category/{category}") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.subject
                    val categoryName = call.parameters["category"]
                    
                    if (userId == null) {
                        call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("Unauthorized"))
                        return@get
                    }
                    
                    if (categoryName == null) {
                        call.respond(HttpStatusCode.BadRequest, ApiResponse.error("Category is required"))
                        return@get
                    }
                    
                    try {
                        val category = com.kace.user.domain.model.PreferenceCategory.valueOf(categoryName.uppercase())
                        val preferences = preferenceService.getPreferencesByCategory(UUID.fromString(userId), category)
                        val response = preferences.map { UserPreferenceResponse.fromDomain(it) }
                        call.respond(ApiResponse.success(response))
                    } catch (e: IllegalArgumentException) {
                        call.respond(HttpStatusCode.BadRequest, ApiResponse.error("Invalid category: $categoryName"))
                    }
                } catch (e: Exception) {
                    logger.error("Get preferences by category error", e)
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse.error("Failed to get preferences: ${e.message}"))
                }
            }
            
            /**
             * 获取特定偏好设置
             */
            get("/{key}") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.subject
                    val key = call.parameters["key"]
                    
                    if (userId == null) {
                        call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("Unauthorized"))
                        return@get
                    }
                    
                    if (key == null) {
                        call.respond(HttpStatusCode.BadRequest, ApiResponse.error("Key is required"))
                        return@get
                    }
                    
                    val preference = preferenceService.getPreference(UUID.fromString(userId), key)
                    
                    if (preference != null) {
                        call.respond(ApiResponse.success(UserPreferenceResponse.fromDomain(preference)))
                    } else {
                        call.respond(HttpStatusCode.NotFound, ApiResponse.error("Preference not found: $key"))
                    }
                } catch (e: Exception) {
                    logger.error("Get preference error", e)
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse.error("Failed to get preference: ${e.message}"))
                }
            }
            
            /**
             * 设置偏好
             */
            post {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.subject
                    
                    if (userId == null) {
                        call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("Unauthorized"))
                        return@post
                    }
                    
                    val request = call.receive<SetPreferenceRequest>()
                    
                    val preference = UserPreference(
                        userId = UUID.fromString(userId),
                        key = request.key,
                        value = request.value,
                        category = request.category
                    )
                    
                    val savedPreference = preferenceService.setPreference(preference)
                    call.respond(ApiResponse.success(UserPreferenceResponse.fromDomain(savedPreference)))
                } catch (e: Exception) {
                    logger.error("Set preference error", e)
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse.error("Failed to set preference: ${e.message}"))
                }
            }
            
            /**
             * 批量设置偏好
             */
            post("/batch") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.subject
                    
                    if (userId == null) {
                        call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("Unauthorized"))
                        return@post
                    }
                    
                    val request = call.receive<SetPreferencesRequest>()
                    
                    val preferences = request.preferences.map {
                        UserPreference(
                            userId = UUID.fromString(userId),
                            key = it.key,
                            value = it.value,
                            category = it.category
                        )
                    }
                    
                    val savedPreferences = preferenceService.setPreferences(preferences)
                    val response = savedPreferences.map { UserPreferenceResponse.fromDomain(it) }
                    call.respond(ApiResponse.success(response))
                } catch (e: Exception) {
                    logger.error("Set preferences batch error", e)
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse.error("Failed to set preferences: ${e.message}"))
                }
            }
            
            /**
             * 删除偏好
             */
            delete("/{key}") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.subject
                    val key = call.parameters["key"]
                    
                    if (userId == null) {
                        call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("Unauthorized"))
                        return@delete
                    }
                    
                    if (key == null) {
                        call.respond(HttpStatusCode.BadRequest, ApiResponse.error("Key is required"))
                        return@delete
                    }
                    
                    val success = preferenceService.deletePreference(UUID.fromString(userId), key)
                    
                    if (success) {
                        call.respond(ApiResponse.success("Preference deleted successfully"))
                    } else {
                        call.respond(HttpStatusCode.NotFound, ApiResponse.error("Preference not found: $key"))
                    }
                } catch (e: Exception) {
                    logger.error("Delete preference error", e)
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse.error("Failed to delete preference: ${e.message}"))
                }
            }
            
            /**
             * 删除类别偏好
             */
            delete("/category/{category}") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.subject
                    val categoryName = call.parameters["category"]
                    
                    if (userId == null) {
                        call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("Unauthorized"))
                        return@delete
                    }
                    
                    if (categoryName == null) {
                        call.respond(HttpStatusCode.BadRequest, ApiResponse.error("Category is required"))
                        return@delete
                    }
                    
                    try {
                        val category = com.kace.user.domain.model.PreferenceCategory.valueOf(categoryName.uppercase())
                        val count = preferenceService.deletePreferencesByCategory(UUID.fromString(userId), category)
                        call.respond(ApiResponse.success("Deleted $count preferences"))
                    } catch (e: IllegalArgumentException) {
                        call.respond(HttpStatusCode.BadRequest, ApiResponse.error("Invalid category: $categoryName"))
                    }
                } catch (e: Exception) {
                    logger.error("Delete preferences by category error", e)
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse.error("Failed to delete preferences: ${e.message}"))
                }
            }
            
            /**
             * 删除所有偏好
             */
            delete {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.subject
                    
                    if (userId == null) {
                        call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("Unauthorized"))
                        return@delete
                    }
                    
                    val count = preferenceService.deleteAllPreferences(UUID.fromString(userId))
                    call.respond(ApiResponse.success("Deleted $count preferences"))
                } catch (e: Exception) {
                    logger.error("Delete all preferences error", e)
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse.error("Failed to delete preferences: ${e.message}"))
                }
            }
            
            /**
             * 导出偏好
             */
            get("/export") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.subject
                    
                    if (userId == null) {
                        call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("Unauthorized"))
                        return@get
                    }
                    
                    val preferencesJson = preferenceService.exportPreferences(UUID.fromString(userId))
                    call.respond(ApiResponse.success(ExportPreferencesResponse(preferencesJson)))
                } catch (e: Exception) {
                    logger.error("Export preferences error", e)
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse.error("Failed to export preferences: ${e.message}"))
                }
            }
            
            /**
             * 导入偏好
             */
            post("/import") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.subject
                    
                    if (userId == null) {
                        call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("Unauthorized"))
                        return@post
                    }
                    
                    val request = call.receive<ImportPreferencesRequest>()
                    val count = preferenceService.importPreferences(
                        UUID.fromString(userId),
                        request.preferences,
                        request.overwrite
                    )
                    
                    call.respond(ApiResponse.success("Imported $count preferences"))
                } catch (e: Exception) {
                    logger.error("Import preferences error", e)
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse.error("Failed to import preferences: ${e.message}"))
                }
            }
        }
    }
} 