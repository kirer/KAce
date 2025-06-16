package com.kace.common.exception

import io.ktor.http.*

/**
 * API异常基类
 */
open class ApiException(
    val statusCode: HttpStatusCode,
    override val message: String,
    val errorCode: String? = null,
    val details: Map<String, String>? = null,
    cause: Throwable? = null
) : RuntimeException(message, cause)

/**
 * 未授权异常
 */
class UnauthorizedException(
    message: String = "Unauthorized",
    errorCode: String? = "UNAUTHORIZED",
    details: Map<String, String>? = null,
    cause: Throwable? = null
) : ApiException(HttpStatusCode.Unauthorized, message, errorCode, details, cause)

/**
 * 禁止访问异常
 */
class ForbiddenException(
    message: String = "Forbidden",
    errorCode: String? = "FORBIDDEN",
    details: Map<String, String>? = null,
    cause: Throwable? = null
) : ApiException(HttpStatusCode.Forbidden, message, errorCode, details, cause)

/**
 * 资源未找到异常
 */
class NotFoundException(
    message: String = "Resource not found",
    errorCode: String? = "NOT_FOUND",
    details: Map<String, String>? = null,
    cause: Throwable? = null
) : ApiException(HttpStatusCode.NotFound, message, errorCode, details, cause)

/**
 * 请求参数无效异常
 */
class BadRequestException(
    message: String = "Bad request",
    errorCode: String? = "BAD_REQUEST",
    details: Map<String, String>? = null,
    cause: Throwable? = null
) : ApiException(HttpStatusCode.BadRequest, message, errorCode, details, cause)

/**
 * 冲突异常
 */
class ConflictException(
    message: String = "Conflict",
    errorCode: String? = "CONFLICT",
    details: Map<String, String>? = null,
    cause: Throwable? = null
) : ApiException(HttpStatusCode.Conflict, message, errorCode, details, cause)

/**
 * 内部服务器错误异常
 */
class InternalServerErrorException(
    message: String = "Internal server error",
    errorCode: String? = "INTERNAL_SERVER_ERROR",
    details: Map<String, String>? = null,
    cause: Throwable? = null
) : ApiException(HttpStatusCode.InternalServerError, message, errorCode, details, cause) 