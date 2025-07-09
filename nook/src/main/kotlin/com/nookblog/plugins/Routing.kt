package com.nookblog.plugins

import com.nookblog.routes
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        authRoutes()
        userRoutes()
        blogRoutes()
        articleRoutes()
    }
}