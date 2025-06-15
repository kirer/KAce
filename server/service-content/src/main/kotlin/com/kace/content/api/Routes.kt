package com.kace.content.api

import com.kace.content.api.controller.*
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRoutes() {
    routing {
        val contentTypeController = ContentTypeController()
        val contentController = ContentController()
        val categoryController = CategoryController()
        val tagController = TagController()
        val contentRelationController = ContentRelationController()
        val contentPermissionController = ContentPermissionController()
        val contentSearchController = ContentSearchController()
        val contentCommentController = ContentCommentController()
        val contentFeedbackController = ContentFeedbackController()
        
        with(contentTypeController) { contentTypeRoutes() }
        with(contentController) { contentRoutes() }
        with(categoryController) { categoryRoutes() }
        with(tagController) { tagRoutes() }
        with(contentRelationController) { contentRelationRoutes() }
        with(contentPermissionController) { contentPermissionRoutes() }
        with(contentSearchController) { contentSearchRoutes() }
        with(contentCommentController) { commentRoutes() }
        with(contentFeedbackController) { feedbackRoutes() }
    }
} 