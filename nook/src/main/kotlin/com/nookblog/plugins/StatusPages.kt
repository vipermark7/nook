package com.nookblog.plugins

import com.nookblog.models.Article
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respond(
                HttpStatusCode.InternalServerError,
                Article.ErrorResponse("Internal server error: ${cause.localizedMessage}")
            )
        }
    }
}