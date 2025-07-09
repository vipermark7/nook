package com.nookblog.routes

import com.nookblog.models.Article
import com.nookblog.services.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authRoutes() {
    val userService = UserService()

    route("/auth") {
        post("/register") {
            try {
                val request = call.receive<Article.CreateUserRequest>()
                val user = userService.createUser(request)

                call.respond(
                    HttpStatusCode.Created,
                    Article.AuthResponse(
                        user = user
                    )
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    Article.ErrorResponse("Registration failed: ${e.localizedMessage}")
                )
            }
        }
    }
}