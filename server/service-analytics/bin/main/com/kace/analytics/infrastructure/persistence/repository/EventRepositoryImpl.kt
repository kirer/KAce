package com.kace.analytics.infrastructure.persistence.repository

import com.kace.analytics.domain.model.Event
import com.kace.analytics.domain.model.EventFilter
import com.kace.analytics.domain.repository.EventRepository
import com.kace.analytics.infrastructure.persistence.entity.EventEntity
import com.kace.analytics.infrastructure.persistence.entity.Events
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.UUID

/**
 * 事件仓库实现类
 */
class EventRepositoryImpl : EventRepository {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun save(event: Event): Event = withContext(Dispatchers.IO) {
        transaction {
            val entity = EventEntity.new(event.id) {
                type = event.type
                name = event.name
                userId = event.userId?.toString()
                sessionId = event.sessionId
                properties = event.properties.let { json.encodeToString(it) }
                timestamp = event.timestamp
                appVersion = event.appVersion
                deviceInfo = event.deviceInfo?.let { json.encodeToString(it) }
                source = event.source
            }
            entity.toEvent()
        }
    }

    override suspend fun saveAll(events: List<Event>): List<Event> = withContext(Dispatchers.IO) {
        transaction {
            events.map { event ->
                val entity = EventEntity.new(event.id) {
                    type = event.type
                    name = event.name
                    userId = event.userId?.toString()
                    sessionId = event.sessionId
                    properties = event.properties.let { json.encodeToString(it) }
                    timestamp = event.timestamp
                    appVersion = event.appVersion
                    deviceInfo = event.deviceInfo?.let { json.encodeToString(it) }
                    source = event.source
                }
                entity.toEvent()
            }
        }
    }

    override suspend fun findById(id: UUID): Event? = withContext(Dispatchers.IO) {
        transaction {
            EventEntity.findById(id)?.toEvent()
        }
    }

    override suspend fun findByFilter(filter: EventFilter, page: Int, size: Int): List<Event> = withContext(Dispatchers.IO) {
        transaction {
            val query = EventEntity.find {
                buildFilterQuery(filter)
            }
            
            query.orderBy(Events.timestamp to SortOrder.DESC)
                 .limit(size, ((page - 1) * size).toLong())
                 .map { it.toEvent() }
        }
    }

    override suspend fun findByUserId(userId: UUID, page: Int, size: Int): List<Event> = withContext(Dispatchers.IO) {
        transaction {
            EventEntity.find { Events.userId eq userId.toString() }
                .orderBy(Events.timestamp to SortOrder.DESC)
                .limit(size, ((page - 1) * size).toLong())
                .map { it.toEvent() }
        }
    }

    override suspend fun findBySessionId(sessionId: String, page: Int, size: Int): List<Event> = withContext(Dispatchers.IO) {
        transaction {
            EventEntity.find { Events.sessionId eq sessionId }
                .orderBy(Events.timestamp to SortOrder.DESC)
                .limit(size, ((page - 1) * size).toLong())
                .map { it.toEvent() }
        }
    }

    override suspend fun findByTimeRange(startTime: Instant, endTime: Instant, page: Int, size: Int): List<Event> = withContext(Dispatchers.IO) {
        transaction {
            EventEntity.find { 
                (Events.timestamp greaterEq startTime) and (Events.timestamp lessEq endTime) 
            }
            .orderBy(Events.timestamp to SortOrder.DESC)
            .limit(size, ((page - 1) * size).toLong())
            .map { it.toEvent() }
        }
    }

    override suspend fun findByType(type: String, page: Int, size: Int): List<Event> = withContext(Dispatchers.IO) {
        transaction {
            EventEntity.find { Events.type eq type }
                .orderBy(Events.timestamp to SortOrder.DESC)
                .limit(size, ((page - 1) * size).toLong())
                .map { it.toEvent() }
        }
    }

    override suspend fun findByName(name: String, page: Int, size: Int): List<Event> = withContext(Dispatchers.IO) {
        transaction {
            EventEntity.find { Events.name eq name }
                .orderBy(Events.timestamp to SortOrder.DESC)
                .limit(size, ((page - 1) * size).toLong())
                .map { it.toEvent() }
        }
    }

    override suspend fun count(filter: EventFilter?): Long = withContext(Dispatchers.IO) {
        transaction {
            if (filter == null) {
                EventEntity.count()
            } else {
                EventEntity.find { buildFilterQuery(filter) }.count()
            }
        }
    }

    override suspend fun deleteOlderThan(time: Instant): Int = withContext(Dispatchers.IO) {
        transaction {
            val entities = EventEntity.find { Events.timestamp lessEq time }
            val count = entities.count().toInt()
            entities.forEach { it.delete() }
            count
        }
    }

    override suspend fun delete(id: UUID): Boolean = withContext(Dispatchers.IO) {
        transaction {
            EventEntity.findById(id)?.let {
                it.delete()
                true
            } ?: false
        }
    }

    override suspend fun aggregate(
        filter: EventFilter,
        groupByFields: List<String>,
        aggregateField: String?,
        aggregationType: String
    ): List<Map<String, Any>> = withContext(Dispatchers.IO) {
        transaction {
            val query = Events.select { buildFilterQuery(filter) }
            
            val groupExpressions = groupByFields.map { fieldName ->
                when (fieldName) {
                    "type" -> Events.type
                    "name" -> Events.name
                    "source" -> Events.source
                    "day" -> Events.timestamp.date()
                    else -> Events.type // 默认用type分组，应该根据实际字段调整
                }
            }
            
            val aggregateExpression = when (aggregationType) {
                "COUNT" -> Count(if (aggregateField != null) Events.id else Events.id)
                "SUM" -> Sum(Events.id, IntegerColumnType()) // 这里需要根据实际字段类型调整
                "AVG" -> Avg(Events.id, IntegerColumnType()) // 这里需要根据实际字段类型调整
                "MAX" -> Max(Events.id, IntegerColumnType()) // 这里需要根据实际字段类型调整
                "MIN" -> Min(Events.id, IntegerColumnType()) // 这里需要根据实际字段类型调整
                else -> Count(Events.id)
            }.alias("value")
            
            val result = query.groupBy(groupExpressions).select(groupExpressions + aggregateExpression)
            
            result.map { row ->
                val map = mutableMapOf<String, Any>()
                groupByFields.forEachIndexed { index, fieldName ->
                    map[fieldName] = row[groupExpressions[index]].toString()
                }
                map["value"] = row[aggregateExpression]
                map
            }
        }
    }

    override suspend fun getTopEvents(filter: EventFilter, limit: Int): List<Map<String, Any>> = withContext(Dispatchers.IO) {
        transaction {
            val query = Events.select { buildFilterQuery(filter) }
                .groupBy(Events.name)
                .select(Events.name, Count(Events.id).alias("count"))
                .orderBy(Count(Events.id), SortOrder.DESC)
                .limit(limit)
                
            query.map { row ->
                mapOf(
                    "name" to row[Events.name],
                    "count" to row[Count(Events.id)]
                )
            }
        }
    }

    override suspend fun getTrend(filter: EventFilter, interval: String): List<Map<String, Any>> = withContext(Dispatchers.IO) {
        transaction {
            val baseDate = filter.startTime ?: Instant.now().minus(30, ChronoUnit.DAYS)
            val endDate = filter.endTime ?: Instant.now()
            
            val dateField = when (interval.lowercase()) {
                "hour" -> date_trunc("hour", Events.timestamp)
                "day" -> date_trunc("day", Events.timestamp)
                "week" -> date_trunc("week", Events.timestamp)
                "month" -> date_trunc("month", Events.timestamp)
                else -> date_trunc("day", Events.timestamp)
            }.alias("date_group")
            
            val query = Events.select { buildFilterQuery(filter) }
                .groupBy(dateField)
                .select(dateField, Count(Events.id).alias("count"))
                .orderBy(dateField)
                
            query.map { row ->
                mapOf(
                    "date" to row[dateField].toString(),
                    "count" to row[Count(Events.id)]
                )
            }
        }
    }

    private fun date_trunc(unit: String, column: Column<Instant>): Expression<LocalDate> {
        // PostgreSQL specific function, might need to adjust for your DB
        return CustomFunction<LocalDate>("date_trunc", VarCharColumnType(), LiteralOp(VarCharColumnType(), unit), column)
    }

    private fun buildFilterQuery(filter: EventFilter): Op<Boolean> {
        var query: Op<Boolean> = Op.TRUE
        
        filter.types?.let { types ->
            if (types.isNotEmpty()) {
                query = query and (Events.type inList types)
            }
        }
        
        filter.names?.let { names ->
            if (names.isNotEmpty()) {
                query = query and (Events.name inList names)
            }
        }
        
        filter.userId?.let { userId ->
            query = query and (Events.userId eq userId.toString())
        }
        
        filter.sessionId?.let { sessionId ->
            query = query and (Events.sessionId eq sessionId)
        }
        
        filter.startTime?.let { startTime ->
            query = query and (Events.timestamp greaterEq startTime)
        }
        
        filter.endTime?.let { endTime ->
            query = query and (Events.timestamp lessEq endTime)
        }
        
        filter.source?.let { source ->
            query = query and (Events.source eq source)
        }
        
        // 处理属性过滤条件需要更复杂的逻辑，可能需要JSON操作
        
        return query
    }
} 