package com.kace.analytics.api.controller

import com.kace.analytics.api.model.request.AggregateMetricRequest
import com.kace.analytics.api.model.request.CreateMetricRequest
import com.kace.analytics.api.model.request.QueryMetricsRequest
import com.kace.analytics.api.model.request.UpdateMetricRequest
import com.kace.analytics.api.model.response.AggregateMetricResponse
import com.kace.analytics.api.model.response.MetricListResponse
import com.kace.analytics.api.model.response.MetricResponse
import com.kace.analytics.domain.model.Metric
import com.kace.analytics.domain.service.MetricService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.UUID

/**
 * 指标控制器
 */
class MetricController(private val metricService: MetricService) {
    
    private val logger = LoggerFactory.getLogger(MetricController::class.java)
    
    /**
     * 配置路由
     */
    fun configureRoutes(routing: Routing) {
        routing.route("/api/v1/metrics") {
            // 创建指标
            post {
                try {
                    val request = call.receive<CreateMetricRequest>()
                    val metric = createMetricFromRequest(request)
                    val createdMetric = metricService.createMetric(metric)
                    call.respond(HttpStatusCode.Created, MetricResponse.fromMetric(createdMetric))
                } catch (e: Exception) {
                    logger.error("Failed to create metric: ${e.message}", e)
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
                }
            }
            
            // 获取单个指标
            get("/{id}") {
                try {
                    val id = call.parameters["id"] ?: throw IllegalArgumentException("Missing metric ID")
                    val metric = metricService.getMetric(UUID.fromString(id))
                    if (metric != null) {
                        call.respond(HttpStatusCode.OK, MetricResponse.fromMetric(metric))
                    } else {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to "Metric not found"))
                    }
                } catch (e: Exception) {
                    logger.error("Failed to get metric: ${e.message}", e)
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
                }
            }
            
            // 更新指标
            put("/{id}") {
                try {
                    val id = call.parameters["id"] ?: throw IllegalArgumentException("Missing metric ID")
                    val request = call.receive<UpdateMetricRequest>()
                    
                    val existingMetric = metricService.getMetric(UUID.fromString(id))
                        ?: return@put call.respond(HttpStatusCode.NotFound, mapOf("error" to "Metric not found"))
                    
                    val updatedMetric = existingMetric.copy(
                        name = request.name ?: existingMetric.name,
                        description = request.description ?: existingMetric.description,
                        value = request.value ?: existingMetric.value,
                        unit = request.unit ?: existingMetric.unit,
                        dimensions = request.dimensions ?: existingMetric.dimensions
                    )
                    
                    val result = metricService.updateMetric(updatedMetric)
                    call.respond(HttpStatusCode.OK, MetricResponse.fromMetric(result))
                } catch (e: Exception) {
                    logger.error("Failed to update metric: ${e.message}", e)
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
                }
            }
            
            // 查询指标
            get {
                try {
                    val request = QueryMetricsRequest(
                        name = call.request.queryParameters["name"],
                        startTime = call.request.queryParameters["startTime"],
                        endTime = call.request.queryParameters["endTime"],
                        limit = call.request.queryParameters["limit"]?.toInt() ?: 20,
                        offset = call.request.queryParameters["offset"]?.toInt() ?: 0
                    )
                    
                    val metrics = when {
                        request.name != null && request.startTime != null && request.endTime != null -> {
                            val startTime = Instant.parse(request.startTime)
                            val endTime = Instant.parse(request.endTime)
                            metricService.getMetricsByTimeRange(startTime, endTime, request.limit, request.offset)
                                .filter { it.name == request.name }
                        }
                        request.name != null -> {
                            metricService.getMetricsByName(request.name, request.limit, request.offset)
                        }
                        request.startTime != null && request.endTime != null -> {
                            val startTime = Instant.parse(request.startTime)
                            val endTime = Instant.parse(request.endTime)
                            metricService.getMetricsByTimeRange(startTime, endTime, request.limit, request.offset)
                        }
                        else -> {
                            metricService.getAllMetrics(request.limit, request.offset)
                        }
                    }
                    
                    val total = when {
                        request.name != null -> {
                            metricService.countMetricsByName(request.name)
                        }
                        else -> {
                            metrics.size.toLong()
                        }
                    }
                    
                    call.respond(HttpStatusCode.OK, MetricListResponse(
                        metrics = metrics.map { MetricResponse.fromMetric(it) },
                        total = total,
                        limit = request.limit,
                        offset = request.offset
                    ))
                } catch (e: Exception) {
                    logger.error("Failed to query metrics: ${e.message}", e)
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
                }
            }
            
            // 聚合指标
            post("/aggregate") {
                try {
                    val request = call.receive<AggregateMetricRequest>()
                    val startTime = Instant.parse(request.startTime)
                    val endTime = Instant.parse(request.endTime)
                    
                    val aggregatedMetric = metricService.calculateAggregatedMetric(
                        name = request.name,
                        startTime = startTime,
                        endTime = endTime,
                        aggregationType = request.aggregationType
                    )
                    
                    call.respond(HttpStatusCode.OK, AggregateMetricResponse(
                        name = aggregatedMetric.name,
                        value = aggregatedMetric.value,
                        unit = aggregatedMetric.unit,
                        aggregationType = request.aggregationType,
                        startTime = request.startTime,
                        endTime = request.endTime,
                        timestamp = aggregatedMetric.timestamp.toString()
                    ))
                } catch (e: Exception) {
                    logger.error("Failed to aggregate metrics: ${e.message}", e)
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
                }
            }
            
            // 删除指标
            delete("/{id}") {
                try {
                    val id = call.parameters["id"] ?: throw IllegalArgumentException("Missing metric ID")
                    val success = metricService.deleteMetric(UUID.fromString(id))
                    if (success) {
                        call.respond(HttpStatusCode.NoContent)
                    } else {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to "Metric not found"))
                    }
                } catch (e: Exception) {
                    logger.error("Failed to delete metric: ${e.message}", e)
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
                }
            }
            
            // 删除时间范围内的指标
            delete {
                try {
                    val startTime = call.request.queryParameters["startTime"] ?: throw IllegalArgumentException("Missing startTime")
                    val endTime = call.request.queryParameters["endTime"] ?: throw IllegalArgumentException("Missing endTime")
                    
                    val start = Instant.parse(startTime)
                    val end = Instant.parse(endTime)
                    
                    val count = metricService.deleteMetricsByTimeRange(start, end)
                    call.respond(HttpStatusCode.OK, mapOf("deletedCount" to count))
                } catch (e: Exception) {
                    logger.error("Failed to delete metrics by time range: ${e.message}", e)
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
                }
            }
        }
    }
    
    /**
     * 从请求创建指标
     */
    private fun createMetricFromRequest(request: CreateMetricRequest): Metric {
        return Metric(
            id = UUID.randomUUID(),
            name = request.name,
            description = request.description,
            value = request.value,
            unit = request.unit,
            dimensions = request.dimensions,
            timestamp = request.timestamp?.let { Instant.parse(it) } ?: Instant.now(),
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
    }
} 