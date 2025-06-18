package com.github.kirer.kace.exception

/**
 * KAce框架基础异常
 */
abstract class KAceException(
    message: String,
    cause: Throwable? = null,
    val errorCode: String,
    val httpStatusCode: Int = 500,
    val details: Map<String, Any> = emptyMap()
) : RuntimeException(message, cause)

/**
 * 系统异常 - 表示系统级别的错误，如配置错误、资源不可用等
 */
open class SystemException(
    message: String,
    cause: Throwable? = null,
    errorCode: String = "SYSTEM_ERROR",
    httpStatusCode: Int = 500,
    details: Map<String, Any> = emptyMap()
) : KAceException(message, cause, errorCode, httpStatusCode, details)

/**
 * 配置异常 - 表示配置相关的错误
 */
class ConfigException(
    message: String,
    cause: Throwable? = null,
    errorCode: String = "CONFIG_ERROR",
    details: Map<String, Any> = emptyMap()
) : SystemException(message, cause, errorCode, 500, details)

/**
 * 插件异常 - 表示插件相关的错误
 */
class PluginException(
    message: String,
    cause: Throwable? = null,
    errorCode: String = "PLUGIN_ERROR",
    httpStatusCode: Int = 500,
    details: Map<String, Any> = emptyMap()
) : SystemException(message, cause, errorCode, httpStatusCode, details)

/**
 * 数据库异常 - 表示数据库操作相关的错误
 */
class DatabaseException(
    message: String,
    cause: Throwable? = null,
    errorCode: String = "DATABASE_ERROR",
    details: Map<String, Any> = emptyMap()
) : SystemException(message, cause, errorCode, 500, details)

/**
 * 业务异常 - 表示业务逻辑相关的错误
 */
open class BusinessException(
    message: String,
    cause: Throwable? = null,
    errorCode: String = "BUSINESS_ERROR",
    httpStatusCode: Int = 400,
    details: Map<String, Any> = emptyMap()
) : KAceException(message, cause, errorCode, httpStatusCode, details)

/**
 * 验证异常 - 表示数据验证相关的错误
 */
class ValidationException(
    message: String,
    cause: Throwable? = null,
    errorCode: String = "VALIDATION_ERROR",
    details: Map<String, Any> = emptyMap()
) : BusinessException(message, cause, errorCode, 400, details)

/**
 * 资源不存在异常 - 表示请求的资源不存在
 */
class ResourceNotFoundException(
    message: String,
    cause: Throwable? = null,
    errorCode: String = "RESOURCE_NOT_FOUND",
    details: Map<String, Any> = emptyMap()
) : BusinessException(message, cause, errorCode, 404, details)

/**
 * 资源冲突异常 - 表示资源冲突，如唯一键冲突
 */
class ResourceConflictException(
    message: String,
    cause: Throwable? = null,
    errorCode: String = "RESOURCE_CONFLICT",
    details: Map<String, Any> = emptyMap()
) : BusinessException(message, cause, errorCode, 409, details)

/**
 * 安全异常 - 表示安全相关的错误，如认证、授权等
 */
open class SecurityException(
    message: String,
    cause: Throwable? = null,
    errorCode: String = "SECURITY_ERROR",
    httpStatusCode: Int = 403,
    details: Map<String, Any> = emptyMap()
) : KAceException(message, cause, errorCode, httpStatusCode, details)

/**
 * 认证异常 - 表示认证失败
 */
class AuthenticationException(
    message: String = "Authentication failed",
    cause: Throwable? = null,
    errorCode: String = "AUTHENTICATION_FAILED",
    details: Map<String, Any> = emptyMap()
) : SecurityException(message, cause, errorCode, 401, details)

/**
 * 授权异常 - 表示授权失败
 */
class AuthorizationException(
    message: String = "Authorization failed",
    cause: Throwable? = null,
    errorCode: String = "AUTHORIZATION_FAILED",
    details: Map<String, Any> = emptyMap()
) : SecurityException(message, cause, errorCode, 403, details)

/**
 * 会话异常 - 表示会话相关的错误
 */
class SessionException(
    message: String,
    cause: Throwable? = null,
    errorCode: String = "SESSION_ERROR",
    details: Map<String, Any> = emptyMap()
) : SecurityException(message, cause, errorCode, 401, details) 