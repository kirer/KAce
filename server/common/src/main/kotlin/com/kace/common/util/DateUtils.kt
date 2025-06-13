package com.kace.common.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * 日期工具类
 */
object DateUtils {
    private val DEFAULT_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val DEFAULT_DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    private val DEFAULT_ZONE_ID = ZoneId.systemDefault()
    
    /**
     * 获取当前时间戳
     */
    fun now(): Instant = Instant.now()
    
    /**
     * 获取当前日期
     */
    fun today(): LocalDate = LocalDate.now()
    
    /**
     * 获取当前日期时间
     */
    fun currentDateTime(): LocalDateTime = LocalDateTime.now()
    
    /**
     * 格式化日期
     */
    fun formatDate(date: LocalDate, pattern: String = "yyyy-MM-dd"): String {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        return date.format(formatter)
    }
    
    /**
     * 格式化日期时间
     */
    fun formatDateTime(dateTime: LocalDateTime, pattern: String = "yyyy-MM-dd HH:mm:ss"): String {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        return dateTime.format(formatter)
    }
    
    /**
     * 解析日期字符串
     */
    fun parseDate(dateString: String, pattern: String = "yyyy-MM-dd"): LocalDate {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        return LocalDate.parse(dateString, formatter)
    }
    
    /**
     * 解析日期时间字符串
     */
    fun parseDateTime(dateTimeString: String, pattern: String = "yyyy-MM-dd HH:mm:ss"): LocalDateTime {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        return LocalDateTime.parse(dateTimeString, formatter)
    }
    
    /**
     * 时间戳转日期时间
     */
    fun instantToLocalDateTime(instant: Instant, zoneId: ZoneId = DEFAULT_ZONE_ID): LocalDateTime {
        return LocalDateTime.ofInstant(instant, zoneId)
    }
    
    /**
     * 日期时间转时间戳
     */
    fun localDateTimeToInstant(localDateTime: LocalDateTime, zoneId: ZoneId = DEFAULT_ZONE_ID): Instant {
        return localDateTime.atZone(zoneId).toInstant()
    }
} 