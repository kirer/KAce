package com.kace.plugin.article

import com.kace.plugin.api.*

/**
 * 文章插件实现
 */
class ArticlePlugin : ContentPlugin {
    override fun getId(): String {
        return "article-plugin"
    }
    
    override fun getName(): String {
        return "文章插件"
    }
    
    override fun getDescription(): String {
        return "提供文章管理功能"
    }
    
    override fun getVersion(): String {
        return "0.1.0"
    }
    
    override fun getAuthor(): String {
        return "KAce Team"
    }
    
    override fun getContentTypes(): List<ContentTypeDefinition> {
        // 定义文章内容类型
        val articleType = ContentTypeDefinition(
            id = "article",
            name = "文章",
            description = "标准文章内容类型",
            fields = listOf(
                FieldDefinition(
                    id = "title",
                    name = "标题",
                    type = FieldType.TEXT,
                    required = true,
                    validations = listOf(
                        ValidationRule(
                            type = ValidationType.MIN_LENGTH,
                            params = mapOf("length" to "2")
                        ),
                        ValidationRule(
                            type = ValidationType.MAX_LENGTH,
                            params = mapOf("length" to "100")
                        )
                    )
                ),
                FieldDefinition(
                    id = "content",
                    name = "内容",
                    type = FieldType.RICH_TEXT,
                    required = true
                ),
                FieldDefinition(
                    id = "summary",
                    name = "摘要",
                    type = FieldType.TEXT,
                    required = false,
                    validations = listOf(
                        ValidationRule(
                            type = ValidationType.MAX_LENGTH,
                            params = mapOf("length" to "200")
                        )
                    )
                ),
                FieldDefinition(
                    id = "cover",
                    name = "封面图",
                    type = FieldType.MEDIA,
                    required = false
                ),
                FieldDefinition(
                    id = "publishDate",
                    name = "发布日期",
                    type = FieldType.DATE,
                    required = true
                ),
                FieldDefinition(
                    id = "author",
                    name = "作者",
                    type = FieldType.TEXT,
                    required = true
                ),
                FieldDefinition(
                    id = "featured",
                    name = "是否推荐",
                    type = FieldType.BOOLEAN,
                    required = false,
                    defaultValue = "false"
                )
            )
        )
        
        return listOf(articleType)
    }
    
    override fun getRoutes(): List<RouteDefinition> {
        return listOf(
            RouteDefinition(
                path = "/api/plugins/article/featured",
                method = HttpMethod.GET,
                handler = "getFeaturedArticles",
                auth = false
            ),
            RouteDefinition(
                path = "/api/plugins/article/recent",
                method = HttpMethod.GET,
                handler = "getRecentArticles",
                auth = false
            ),
            RouteDefinition(
                path = "/api/plugins/article/author/{authorId}",
                method = HttpMethod.GET,
                handler = "getArticlesByAuthor",
                auth = false
            ),
            RouteDefinition(
                path = "/api/plugins/article/stats",
                method = HttpMethod.GET,
                handler = "getArticleStats",
                auth = true
            )
        )
    }
    
    override fun handleEvent(event: PluginEvent) {
        when (event.type) {
            "content.created" -> {
                println("文章插件收到内容创建事件: ${event.payload}")
            }
            "content.updated" -> {
                println("文章插件收到内容更新事件: ${event.payload}")
            }
            "content.deleted" -> {
                println("文章插件收到内容删除事件: ${event.payload}")
            }
            else -> {
                // 忽略其他事件
            }
        }
    }
    
    override fun initialize() {
        println("文章插件初始化")
    }
    
    override fun destroy() {
        println("文章插件销毁")
    }
} 