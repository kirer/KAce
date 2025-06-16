package com.kace.notification.infrastructure.template

import freemarker.template.Configuration
import freemarker.template.Template
import freemarker.template.TemplateException
import org.slf4j.LoggerFactory
import java.io.IOException
import java.io.StringReader
import java.io.StringWriter

/**
 * Freemarker模板引擎实现
 */
class FreemarkerTemplateEngine : TemplateEngine {
    
    private val logger = LoggerFactory.getLogger(FreemarkerTemplateEngine::class.java)
    private val configuration: Configuration
    
    init {
        configuration = Configuration(Configuration.VERSION_2_3_31).apply {
            defaultEncoding = "UTF-8"
            logTemplateExceptions = false
            wrapUncheckedExceptions = true
        }
    }
    
    override fun render(template: String, variables: Map<String, Any>): String {
        try {
            val templateObj = Template("inline", StringReader(template), configuration)
            val writer = StringWriter()
            templateObj.process(variables, writer)
            return writer.toString()
        } catch (e: IOException) {
            logger.error("模板读取错误: ${e.message}")
            throw RuntimeException("模板读取错误", e)
        } catch (e: TemplateException) {
            logger.error("模板渲染错误: ${e.message}")
            throw RuntimeException("模板渲染错误", e)
        }
    }
} 