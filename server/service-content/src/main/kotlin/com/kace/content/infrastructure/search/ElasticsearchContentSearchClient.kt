package com.kace.content.infrastructure.search

import com.kace.content.domain.model.Content
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest
import org.elasticsearch.action.bulk.BulkRequest
import org.elasticsearch.action.delete.DeleteRequest
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.client.indices.CreateIndexRequest
import org.elasticsearch.client.indices.GetIndexRequest
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.xcontent.XContentType
import org.elasticsearch.index.query.BoolQueryBuilder
import org.elasticsearch.index.query.MatchQueryBuilder
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.index.query.TermQueryBuilder
import org.elasticsearch.index.reindex.DeleteByQueryRequest
import org.elasticsearch.search.builder.SearchSourceBuilder
import org.slf4j.LoggerFactory
import java.util.UUID

/**
 * 基于Elasticsearch的内容搜索客户端
 */
class ElasticsearchContentSearchClient(
    private val client: RestHighLevelClient,
    private val indexName: String
) : ContentSearchClient {
    private val logger = LoggerFactory.getLogger(ElasticsearchContentSearchClient::class.java)
    
    /**
     * 创建索引
     */
    override suspend fun createIndex(): Boolean {
        logger.info("创建索引: $indexName")
        
        try {
            // 检查索引是否存在
            val indexExists = client.indices().exists(GetIndexRequest(indexName), RequestOptions.DEFAULT)
            if (indexExists) {
                logger.info("索引已存在: $indexName")
                return true
            }
            
            // 创建索引
            val request = CreateIndexRequest(indexName)
            request.settings(
                Settings.builder()
                    .put("index.number_of_shards", 3)
                    .put("index.number_of_replicas", 1)
            )
            
            // 设置映射
            val mapping = """
                {
                  "properties": {
                    "id": { "type": "keyword" },
                    "contentTypeId": { "type": "keyword" },
                    "title": { 
                      "type": "text",
                      "analyzer": "standard",
                      "fields": {
                        "keyword": { "type": "keyword", "ignore_above": 256 }
                      }
                    },
                    "slug": { "type": "keyword" },
                    "status": { "type": "keyword" },
                    "languageCode": { "type": "keyword" },
                    "fields": { "type": "object", "dynamic": true },
                    "categoryIds": { "type": "keyword" },
                    "tagIds": { "type": "keyword" },
                    "createdAt": { "type": "date" },
                    "updatedAt": { "type": "date" },
                    "publishedAt": { "type": "date" },
                    "createdBy": { "type": "keyword" }
                  }
                }
            """.trimIndent()
            
            request.mapping(mapping, XContentType.JSON)
            
            val response = client.indices().create(request, RequestOptions.DEFAULT)
            return response.isAcknowledged
        } catch (e: Exception) {
            logger.error("创建索引失败: $indexName", e)
            return false
        }
    }
    
    /**
     * 删除索引
     */
    override suspend fun deleteIndex(): Boolean {
        logger.info("删除索引: $indexName")
        
        try {
            // 检查索引是否存在
            val indexExists = client.indices().exists(GetIndexRequest(indexName), RequestOptions.DEFAULT)
            if (!indexExists) {
                logger.info("索引不存在: $indexName")
                return true
            }
            
            // 删除索引
            val request = DeleteIndexRequest(indexName)
            val response = client.indices().delete(request, RequestOptions.DEFAULT)
            return response.isAcknowledged
        } catch (e: Exception) {
            logger.error("删除索引失败: $indexName", e)
            return false
        }
    }
    
    /**
     * 索引内容
     */
    override suspend fun indexContent(content: Content): Boolean {
        logger.info("索引内容: ${content.id}")
        
        try {
            // 构建索引文档
            val document = mapOf(
                "id" to content.id.toString(),
                "contentTypeId" to content.contentTypeId.toString(),
                "title" to content.title,
                "slug" to content.slug,
                "status" to content.status.name,
                "languageCode" to content.languageCode,
                "fields" to content.fields,
                "categoryIds" to content.categoryIds.map { it.toString() },
                "tagIds" to content.tagIds.map { it.toString() },
                "createdAt" to content.createdAt,
                "updatedAt" to content.updatedAt,
                "publishedAt" to content.publishedAt,
                "createdBy" to content.createdBy.toString()
            )
            
            // 创建索引请求
            val request = IndexRequest(indexName)
                .id(content.id.toString())
                .source(document)
            
            // 执行索引请求
            val response = client.index(request, RequestOptions.DEFAULT)
            return response.result().name.equals("created", ignoreCase = true) ||
                    response.result().name.equals("updated", ignoreCase = true)
        } catch (e: Exception) {
            logger.error("索引内容失败: ${content.id}", e)
            return false
        }
    }
    
    /**
     * 删除内容索引
     */
    override suspend fun deleteContent(id: UUID): Boolean {
        logger.info("删除内容索引: $id")
        
        try {
            // 创建删除请求
            val request = DeleteRequest(indexName, id.toString())
            
            // 执行删除请求
            val response = client.delete(request, RequestOptions.DEFAULT)
            return response.result().name.equals("deleted", ignoreCase = true)
        } catch (e: Exception) {
            logger.error("删除内容索引失败: $id", e)
            return false
        }
    }
    
    /**
     * 删除指定类型的所有内容索引
     */
    override suspend fun deleteContentByType(contentTypeId: UUID): Boolean {
        logger.info("删除内容类型索引: $contentTypeId")
        
        try {
            // 创建删除请求
            val request = DeleteByQueryRequest(indexName)
            request.setQuery(QueryBuilders.termQuery("contentTypeId", contentTypeId.toString()))
            
            // 执行删除请求
            val response = client.deleteByQuery(request, RequestOptions.DEFAULT)
            return response.deleted > 0
        } catch (e: Exception) {
            logger.error("删除内容类型索引失败: $contentTypeId", e)
            return false
        }
    }
    
    /**
     * 搜索内容
     */
    override suspend fun searchContent(
        query: String,
        contentTypeId: UUID?,
        status: String?,
        languageCode: String?,
        categoryId: UUID?,
        tagId: UUID?,
        offset: Int,
        limit: Int
    ): List<UUID> {
        logger.info("搜索内容: $query")
        
        try {
            // 创建搜索请求
            val searchRequest = SearchRequest(indexName)
            val searchSourceBuilder = SearchSourceBuilder()
            
            // 构建查询
            val boolQuery = QueryBuilders.boolQuery()
            
            // 添加搜索条件
            if (query.isNotBlank()) {
                val multiMatchQuery = QueryBuilders.multiMatchQuery(query, "title", "fields.*")
                    .fuzziness("AUTO")
                boolQuery.must(multiMatchQuery)
            } else {
                boolQuery.must(QueryBuilders.matchAllQuery())
            }
            
            // 添加过滤条件
            contentTypeId?.let {
                boolQuery.filter(QueryBuilders.termQuery("contentTypeId", it.toString()))
            }
            
            status?.let {
                boolQuery.filter(QueryBuilders.termQuery("status", it))
            }
            
            languageCode?.let {
                boolQuery.filter(QueryBuilders.termQuery("languageCode", it))
            }
            
            categoryId?.let {
                boolQuery.filter(QueryBuilders.termQuery("categoryIds", it.toString()))
            }
            
            tagId?.let {
                boolQuery.filter(QueryBuilders.termQuery("tagIds", it.toString()))
            }
            
            // 设置查询
            searchSourceBuilder.query(boolQuery)
            
            // 设置分页
            searchSourceBuilder.from(offset)
            searchSourceBuilder.size(limit)
            
            // 设置排序
            searchSourceBuilder.sort("_score")
            searchSourceBuilder.sort("updatedAt", org.elasticsearch.search.sort.SortOrder.DESC)
            
            // 设置返回字段
            searchSourceBuilder.fetchSource(arrayOf("id"), null)
            
            // 执行查询
            searchRequest.source(searchSourceBuilder)
            val searchResponse = client.search(searchRequest, RequestOptions.DEFAULT)
            
            // 解析结果
            return searchResponse.hits.hits.mapNotNull { hit ->
                val idStr = (hit.sourceAsMap["id"] as? String) ?: return@mapNotNull null
                try {
                    UUID.fromString(idStr)
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            logger.error("搜索内容失败: $query", e)
            return emptyList()
        }
    }
    
    /**
     * 获取搜索结果数量
     */
    override suspend fun countSearchResults(
        query: String,
        contentTypeId: UUID?,
        status: String?,
        languageCode: String?,
        categoryId: UUID?,
        tagId: UUID?
    ): Long {
        logger.info("计算搜索结果数量: $query")
        
        try {
            // 创建搜索请求
            val searchRequest = SearchRequest(indexName)
            val searchSourceBuilder = SearchSourceBuilder()
            
            // 构建查询
            val boolQuery = QueryBuilders.boolQuery()
            
            // 添加搜索条件
            if (query.isNotBlank()) {
                val multiMatchQuery = QueryBuilders.multiMatchQuery(query, "title", "fields.*")
                    .fuzziness("AUTO")
                boolQuery.must(multiMatchQuery)
            } else {
                boolQuery.must(QueryBuilders.matchAllQuery())
            }
            
            // 添加过滤条件
            contentTypeId?.let {
                boolQuery.filter(QueryBuilders.termQuery("contentTypeId", it.toString()))
            }
            
            status?.let {
                boolQuery.filter(QueryBuilders.termQuery("status", it))
            }
            
            languageCode?.let {
                boolQuery.filter(QueryBuilders.termQuery("languageCode", it))
            }
            
            categoryId?.let {
                boolQuery.filter(QueryBuilders.termQuery("categoryIds", it.toString()))
            }
            
            tagId?.let {
                boolQuery.filter(QueryBuilders.termQuery("tagIds", it.toString()))
            }
            
            // 设置查询
            searchSourceBuilder.query(boolQuery)
            
            // 设置大小为0，只获取总数
            searchSourceBuilder.size(0)
            
            // 执行查询
            searchRequest.source(searchSourceBuilder)
            val searchResponse = client.search(searchRequest, RequestOptions.DEFAULT)
            
            // 返回总数
            return searchResponse.hits.totalHits?.value ?: 0
        } catch (e: Exception) {
            logger.error("计算搜索结果数量失败: $query", e)
            return 0
        }
    }
    
    /**
     * 查找相关内容
     */
    override suspend fun findRelatedContent(
        contentId: UUID,
        contentTypeId: UUID,
        title: String,
        fields: Map<String, String>,
        limit: Int
    ): List<UUID> {
        logger.info("查找相关内容: $contentId")
        
        try {
            // 创建搜索请求
            val searchRequest = SearchRequest(indexName)
            val searchSourceBuilder = SearchSourceBuilder()
            
            // 构建查询
            val boolQuery = QueryBuilders.boolQuery()
            
            // 添加搜索条件
            val titleQuery = QueryBuilders.matchQuery("title", title)
                .fuzziness("AUTO")
                .boost(2.0f)
            boolQuery.should(titleQuery)
            
            // 添加字段内容作为搜索条件
            fields.forEach { (key, value) ->
                val fieldQuery = QueryBuilders.matchQuery("fields.$key", value)
                    .fuzziness("AUTO")
                boolQuery.should(fieldQuery)
            }
            
            // 添加过滤条件
            // 同类型内容
            boolQuery.filter(QueryBuilders.termQuery("contentTypeId", contentTypeId.toString()))
            
            // 排除当前内容
            boolQuery.mustNot(QueryBuilders.termQuery("id", contentId.toString()))
            
            // 只返回已发布内容
            boolQuery.filter(QueryBuilders.termQuery("status", "PUBLISHED"))
            
            // 设置查询
            searchSourceBuilder.query(boolQuery)
            
            // 设置返回数量
            searchSourceBuilder.size(limit)
            
            // 设置返回字段
            searchSourceBuilder.fetchSource(arrayOf("id"), null)
            
            // 执行查询
            searchRequest.source(searchSourceBuilder)
            val searchResponse = client.search(searchRequest, RequestOptions.DEFAULT)
            
            // 解析结果
            return searchResponse.hits.hits.mapNotNull { hit ->
                val idStr = (hit.sourceAsMap["id"] as? String) ?: return@mapNotNull null
                try {
                    UUID.fromString(idStr)
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            logger.error("查找相关内容失败: $contentId", e)
            return emptyList()
        }
    }
} 