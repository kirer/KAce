package com.kace.content.infrastructure.config

import com.kace.content.domain.service.ContentSearchService
import com.kace.content.domain.service.impl.ContentSearchServiceImpl
import com.kace.content.infrastructure.search.ContentSearchClient
import com.kace.content.infrastructure.search.ElasticsearchContentSearchClient
import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestHighLevelClient
import org.koin.dsl.module

/**
 * 搜索配置
 */
object SearchConfig {
    /**
     * 搜索模块配置
     */
    val searchModule = module {
        // Elasticsearch客户端
        single {
            val config = get<AppConfig>()
            val hosts = config.elasticsearch.hosts.map { host ->
                val parts = host.split(":")
                val hostname = parts[0]
                val port = if (parts.size > 1) parts[1].toInt() else 9200
                HttpHost(hostname, port, "http")
            }.toTypedArray()
            
            RestHighLevelClient(RestClient.builder(*hosts))
        }
        
        // 内容搜索客户端
        single<ContentSearchClient> {
            val config = get<AppConfig>()
            ElasticsearchContentSearchClient(
                client = get(),
                indexName = config.elasticsearch.contentIndex
            )
        }
        
        // 内容搜索服务
        single<ContentSearchService> {
            ContentSearchServiceImpl(
                contentRepository = get(),
                searchClient = get()
            )
        }
    }
} 