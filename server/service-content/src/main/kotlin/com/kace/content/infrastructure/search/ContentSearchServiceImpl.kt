package com.kace.content.infrastructure.search

import com.kace.content.domain.model.Content
import com.kace.content.domain.model.ContentStatus
import com.kace.content.domain.repository.ContentRepository
import com.kace.content.domain.service.ContentSearchService
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import org.slf4j.LoggerFactory
import java.util.UUID

/**
 * 内容搜索服务实现
 */
class ContentSearchServiceImpl(
    private val elasticsearchClient: ElasticsearchClient,
    private val contentRepository: ContentRepository,
    private val json: Json
) : ContentSearchService {
    private val logger = LoggerFactory.getLogger(ContentSearchServiceImpl::class.java)
    private val indexName = "contents"
    
    /**
     * 初始化索引
     */
    suspend fun initializeIndex() {
        if (!elasticsearchClient.indexExists(indexName)) {
            val mappings = """
                {
                  "mappings": {
                    "properties": {
                      "id": { "type": "keyword" },
                      "contentTypeId": { "type": "keyword" },
                      "title": { "type": "text", "analyzer": "standard" },
                      "slug": { "type": "keyword" },
                      "status": { "type": "keyword" },
                      "createdBy": { "type": "keyword" },
                      "createdAt": { "type": "date" },
                      "updatedAt": { "type": "date" },
                      "publishedAt": { "type": "date" },
                      "version": { "type": "integer" },
                      "languageCode": { "type": "keyword" },
                      "fields": { "type": "object", "dynamic": true }
                    }
                  }
                }
            """.trimIndent()
            
            val result = elasticsearchClient.createIndex(indexName, mappings)
            if (result) {
                logger.info("内容索引创建成功")
            } else {
                logger.error("内容索引创建失败")
            }
        }
    }
    
    /**
     * 搜索内容
     */
    override suspend fun searchContent(
        query: String,
        contentTypeId: UUID?,
        status: ContentStatus?,
        languageCode: String?,
        categoryId: UUID?,
        tagId: UUID?,
        offset: Int,
        limit: Int
    ): List<Content> {
        val searchQuery = buildJsonObject {
            put("from", offset)
            put("size", limit)
            put("query", buildJsonObject {
                put("bool", buildJsonObject {
                    put("must", buildJsonObject {
                        put("multi_match", buildJsonObject {
                            put("query", query)
                            put("fields", buildJsonObject {
                                put("title", 3.0)
                                put("fields.*", 1.0)
                            })
                        })
                    })
                    put("filter", buildJsonObject {
                        put("bool", buildJsonObject {
                            val filters = mutableListOf<JsonObject>()
                            
                            contentTypeId?.let {
                                filters.add(buildJsonObject {
                                    put("term", buildJsonObject {
                                        put("contentTypeId", it.toString())
                                    })
                                })
                            }
                            
                            status?.let {
                                filters.add(buildJsonObject {
                                    put("term", buildJsonObject {
                                        put("status", it.name)
                                    })
                                })
                            }
                            
                            languageCode?.let {
                                filters.add(buildJsonObject {
                                    put("term", buildJsonObject {
                                        put("languageCode", it)
                                    })
                                })
                            }
                            
                            put("must", buildJsonObject {
                                put("match_all", buildJsonObject {})
                            })
                        })
                    })
                })
            })
        }
        
        val searchResult = elasticsearchClient.searchDocuments(indexName, json.encodeToString(searchQuery))
        val searchResultJson = json.parseToJsonElement(searchResult).jsonObject
        
        val hits = searchResultJson["hits"]?.jsonObject?.get("hits")?.jsonArray ?: return emptyList()
        
        val contentIds = hits.mapNotNull { hit ->
            val source = hit.jsonObject["_source"]?.jsonObject
            source?.get("id")?.jsonPrimitive?.content?.let { UUID.fromString(it) }
        }
        
        // 从数据库中获取完整的内容对象
        val contents = contentIds.mapNotNull { contentRepository.getById(it) }
        
        // 如果需要根据分类或标签过滤，在这里进行额外的过滤
        return contents
    }
    
    /**
     * 获取搜索结果数量
     */
    override suspend fun countSearchResults(
        query: String,
        contentTypeId: UUID?,
        status: ContentStatus?,
        languageCode: String?,
        categoryId: UUID?,
        tagId: UUID?
    ): Long {
        val searchQuery = buildJsonObject {
            put("size", 0)
            put("query", buildJsonObject {
                put("bool", buildJsonObject {
                    put("must", buildJsonObject {
                        put("multi_match", buildJsonObject {
                            put("query", query)
                            put("fields", buildJsonObject {
                                put("title", 3.0)
                                put("fields.*", 1.0)
                            })
                        })
                    })
                    put("filter", buildJsonObject {
                        put("bool", buildJsonObject {
                            val filters = mutableListOf<JsonObject>()
                            
                            contentTypeId?.let {
                                filters.add(buildJsonObject {
                                    put("term", buildJsonObject {
                                        put("contentTypeId", it.toString())
                                    })
                                })
                            }
                            
                            status?.let {
                                filters.add(buildJsonObject {
                                    put("term", buildJsonObject {
                                        put("status", it.name)
                                    })
                                })
                            }
                            
                            languageCode?.let {
                                filters.add(buildJsonObject {
                                    put("term", buildJsonObject {
                                        put("languageCode", it)
                                    })
                                })
                            }
                            
                            put("must", buildJsonObject {
                                put("match_all", buildJsonObject {})
                            })
                        })
                    })
                })
            })
        }
        
        val searchResult = elasticsearchClient.searchDocuments(indexName, json.encodeToString(searchQuery))
        val searchResultJson = json.parseToJsonElement(searchResult).jsonObject
        
        return searchResultJson["hits"]?.jsonObject?.get("total")?.jsonObject?.get("value")?.jsonPrimitive?.content?.toLongOrNull() ?: 0L
    }
    
    /**
     * 索引内容
     */
    override suspend fun indexContent(content: Content): Boolean {
        val contentJson = buildJsonObject {
            put("id", content.id.toString())
            put("contentTypeId", content.contentTypeId.toString())
            put("title", content.title)
            put("slug", content.slug)
            put("status", content.status.name)
            put("createdBy", content.createdBy.toString())
            put("createdAt", content.createdAt)
            put("updatedAt", content.updatedAt)
            put("publishedAt", content.publishedAt)
            put("version", content.version)
            put("languageCode", content.languageCode)
            put("fields", buildJsonObject {
                content.fields.forEach { (key, value) ->
                    put(key, value)
                }
            })
        }
        
        return elasticsearchClient.indexDocument(indexName, content.id.toString(), json.encodeToString(contentJson))
    }
    
    /**
     * 更新内容索引
     */
    override suspend fun updateContentIndex(content: Content): Boolean {
        return indexContent(content)
    }
    
    /**
     * 删除内容索引
     */
    override suspend fun deleteContentIndex(contentId: UUID): Boolean {
        return elasticsearchClient.deleteDocument(indexName, contentId.toString())
    }
    
    /**
     * 重建所有索引
     */
    override suspend fun rebuildIndex(): Boolean {
        try {
            // 删除并重新创建索引
            elasticsearchClient.deleteIndex(indexName)
            initializeIndex()
            
            // 批量索引所有内容
            var offset = 0
            val limit = 100
            var hasMore = true
            
            while (hasMore) {
                val contents = contentRepository.getAll(offset = offset, limit = limit)
                if (contents.isEmpty()) {
                    hasMore = false
                } else {
                    contents.forEach { content ->
                        indexContent(content)
                    }
                    offset += limit
                }
            }
            
            return true
        } catch (e: Exception) {
            logger.error("重建索引失败", e)
            return false
        }
    }
} 