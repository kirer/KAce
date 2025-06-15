package com.kace.content.infrastructure.search

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

/**
 * Elasticsearch客户端
 */
class ElasticsearchClient(
    private val host: String,
    private val port: Int,
    private val username: String? = null,
    private val password: String? = null,
    private val indexPrefix: String = "kace-content"
) {
    private val logger = LoggerFactory.getLogger(ElasticsearchClient::class.java)
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }
    
    private val baseUrl = "http://$host:$port"
    
    /**
     * 创建索引
     */
    suspend fun createIndex(indexName: String, mappings: String): Boolean {
        val fullIndexName = "$indexPrefix-$indexName"
        try {
            val response = client.put("$baseUrl/$fullIndexName") {
                contentType(ContentType.Application.Json)
                if (username != null && password != null) {
                    basicAuth(username, password)
                }
                setBody(mappings)
            }
            return response.status.isSuccess()
        } catch (e: Exception) {
            logger.error("创建索引失败: $fullIndexName", e)
            return false
        }
    }
    
    /**
     * 删除索引
     */
    suspend fun deleteIndex(indexName: String): Boolean {
        val fullIndexName = "$indexPrefix-$indexName"
        try {
            val response = client.delete("$baseUrl/$fullIndexName") {
                if (username != null && password != null) {
                    basicAuth(username, password)
                }
            }
            return response.status.isSuccess()
        } catch (e: Exception) {
            logger.error("删除索引失败: $fullIndexName", e)
            return false
        }
    }
    
    /**
     * 索引文档
     */
    suspend fun indexDocument(indexName: String, id: String, document: String): Boolean {
        val fullIndexName = "$indexPrefix-$indexName"
        try {
            val response = client.put("$baseUrl/$fullIndexName/_doc/$id") {
                contentType(ContentType.Application.Json)
                if (username != null && password != null) {
                    basicAuth(username, password)
                }
                setBody(document)
            }
            return response.status.isSuccess()
        } catch (e: Exception) {
            logger.error("索引文档失败: $fullIndexName, id: $id", e)
            return false
        }
    }
    
    /**
     * 更新文档
     */
    suspend fun updateDocument(indexName: String, id: String, document: String): Boolean {
        val fullIndexName = "$indexPrefix-$indexName"
        try {
            val response = client.post("$baseUrl/$fullIndexName/_update/$id") {
                contentType(ContentType.Application.Json)
                if (username != null && password != null) {
                    basicAuth(username, password)
                }
                setBody("""{"doc": $document}""")
            }
            return response.status.isSuccess()
        } catch (e: Exception) {
            logger.error("更新文档失败: $fullIndexName, id: $id", e)
            return false
        }
    }
    
    /**
     * 删除文档
     */
    suspend fun deleteDocument(indexName: String, id: String): Boolean {
        val fullIndexName = "$indexPrefix-$indexName"
        try {
            val response = client.delete("$baseUrl/$fullIndexName/_doc/$id") {
                if (username != null && password != null) {
                    basicAuth(username, password)
                }
            }
            return response.status.isSuccess()
        } catch (e: Exception) {
            logger.error("删除文档失败: $fullIndexName, id: $id", e)
            return false
        }
    }
    
    /**
     * 搜索文档
     */
    suspend fun searchDocuments(indexName: String, query: String): String {
        val fullIndexName = "$indexPrefix-$indexName"
        try {
            val response = client.post("$baseUrl/$fullIndexName/_search") {
                contentType(ContentType.Application.Json)
                if (username != null && password != null) {
                    basicAuth(username, password)
                }
                setBody(query)
            }
            return response.bodyAsText()
        } catch (e: Exception) {
            logger.error("搜索文档失败: $fullIndexName", e)
            throw e
        }
    }
    
    /**
     * 检查索引是否存在
     */
    suspend fun indexExists(indexName: String): Boolean {
        val fullIndexName = "$indexPrefix-$indexName"
        try {
            val response = client.head("$baseUrl/$fullIndexName") {
                if (username != null && password != null) {
                    basicAuth(username, password)
                }
            }
            return response.status.isSuccess()
        } catch (e: Exception) {
            logger.error("检查索引是否存在失败: $fullIndexName", e)
            return false
        }
    }
} 