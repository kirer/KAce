package com.kace.plugin.product

import com.kace.plugin.api.*

/**
 * 产品插件实现
 */
class ProductPlugin : ContentPlugin {
    override fun getId(): String {
        return "product-plugin"
    }
    
    override fun getName(): String {
        return "产品插件"
    }
    
    override fun getDescription(): String {
        return "提供产品管理功能"
    }
    
    override fun getVersion(): String {
        return "0.1.0"
    }
    
    override fun getAuthor(): String {
        return "KAce Team"
    }
    
    override fun getContentTypes(): List<ContentTypeDefinition> {
        // 定义产品内容类型
        val productType = ContentTypeDefinition(
            id = "product",
            name = "产品",
            description = "产品内容类型",
            fields = listOf(
                FieldDefinition(
                    id = "name",
                    name = "产品名称",
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
                    id = "description",
                    name = "产品描述",
                    type = FieldType.RICH_TEXT,
                    required = true
                ),
                FieldDefinition(
                    id = "price",
                    name = "价格",
                    type = FieldType.NUMBER,
                    required = true,
                    validations = listOf(
                        ValidationRule(
                            type = ValidationType.MIN_VALUE,
                            params = mapOf("value" to "0")
                        )
                    )
                ),
                FieldDefinition(
                    id = "images",
                    name = "产品图片",
                    type = FieldType.MEDIA,
                    required = true
                ),
                FieldDefinition(
                    id = "sku",
                    name = "SKU",
                    type = FieldType.TEXT,
                    required = true
                ),
                FieldDefinition(
                    id = "inventory",
                    name = "库存",
                    type = FieldType.NUMBER,
                    required = true,
                    defaultValue = "0"
                ),
                FieldDefinition(
                    id = "featured",
                    name = "是否推荐",
                    type = FieldType.BOOLEAN,
                    required = false,
                    defaultValue = "false"
                ),
                FieldDefinition(
                    id = "specifications",
                    name = "规格参数",
                    type = FieldType.JSON,
                    required = false
                )
            )
        )
        
        // 定义产品分类内容类型
        val productCategoryType = ContentTypeDefinition(
            id = "product-category",
            name = "产品分类",
            description = "产品分类内容类型",
            fields = listOf(
                FieldDefinition(
                    id = "name",
                    name = "分类名称",
                    type = FieldType.TEXT,
                    required = true
                ),
                FieldDefinition(
                    id = "description",
                    name = "分类描述",
                    type = FieldType.TEXT,
                    required = false
                ),
                FieldDefinition(
                    id = "image",
                    name = "分类图片",
                    type = FieldType.MEDIA,
                    required = false
                ),
                FieldDefinition(
                    id = "parentCategory",
                    name = "父分类",
                    type = FieldType.REFERENCE,
                    required = false
                )
            )
        )
        
        return listOf(productType, productCategoryType)
    }
    
    override fun getRoutes(): List<RouteDefinition> {
        return listOf(
            RouteDefinition(
                path = "/api/plugins/product/featured",
                method = HttpMethod.GET,
                handler = "getFeaturedProducts",
                auth = false
            ),
            RouteDefinition(
                path = "/api/plugins/product/category/{categoryId}",
                method = HttpMethod.GET,
                handler = "getProductsByCategory",
                auth = false
            ),
            RouteDefinition(
                path = "/api/plugins/product/search",
                method = HttpMethod.GET,
                handler = "searchProducts",
                auth = false
            ),
            RouteDefinition(
                path = "/api/plugins/product/stats",
                method = HttpMethod.GET,
                handler = "getProductStats",
                auth = true
            )
        )
    }
    
    override fun handleEvent(event: PluginEvent) {
        when (event.type) {
            "product.created" -> {
                println("产品插件收到产品创建事件: ${event.payload}")
            }
            "product.updated" -> {
                println("产品插件收到产品更新事件: ${event.payload}")
            }
            "product.deleted" -> {
                println("产品插件收到产品删除事件: ${event.payload}")
            }
            "order.created" -> {
                println("产品插件收到订单创建事件: ${event.payload}")
            }
            else -> {
                // 忽略其他事件
            }
        }
    }
    
    override fun initialize() {
        println("产品插件初始化")
    }
    
    override fun destroy() {
        println("产品插件销毁")
    }
} 