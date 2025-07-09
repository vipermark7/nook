package com.nookblog.com.nookblog.routes

import com.nookblog.models.Article
import com.nookblog.models.blogDto
import com.nookblog.services.BlogService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.blogRoutes() {
    val blogService = BlogService()

    route("/blogs") {
        get {
            val blogs = blogService.getAllBlogs()
            call.respond(blogs)
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    Article.ErrorResponse("Invalid blog ID")
                )
                return@get
            }

            val blog = blogService.getBlogById(id)
            if (blog != null) {
                call.respond(blog.blogDto())
            } else {
                call.respond(
                    HttpStatusCode.NotFound,
                    Article.ErrorResponse("Blog not found")
                )
            }
        }

        authenticate("auth-basic") {
            post {
                val principal = call.principal<UserIdPrincipal>()
                val userId = principal?.name?.toIntOrNull()

                if (userId == null) {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        Article.ErrorResponse("Invalid credentials")
                    )
                    return@post
                }

                // Check if user already has a blog
                val existingBlog = blogService.getBlogByUserId(userId)
                if (existingBlog != null) {
                    call.respond(
                        HttpStatusCode.Conflict,
                        Article.ErrorResponse("User already has a blog")
                    )
                    return@post
                }

                try {
                    val request = call.receive<Article.CreateBlogRequest>()
                    val blog = blogService.createBlog(userId, request)
                    call.respond(HttpStatusCode.Created, blog)
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        Article.ErrorResponse("Failed to create blog: ${e.localizedMessage}")
                    )
                }
            }

            get("/my") {
                val principal = call.principal<UserIdPrincipal>()
                val userId = principal?.name?.toIntOrNull()

                if (userId != null) {
                    val blog = blogService.getBlogByUserId(userId)
                    if (blog != null) {
                        call.respond(blog)
                    } else {
                        call.respond(
                            HttpStatusCode.NotFound,
                            Article.ErrorResponse("Blog not found")
                        )
                    }
                } else {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        Article.ErrorResponse("Invalid credentials")
                    )
                }
            }

            put("/{id}") {
                val principal = call.principal<UserIdPrincipal>()
                val userId = principal?.name?.toIntOrNull()
                val blogId = call.parameters["id"]?.toIntOrNull()

                if (userId == null || blogId == null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        Article.ErrorResponse("Invalid request")
                    )
                    return@put
                }

                val blog = blogService.getBlogById(blogId)
                if (blog == null) {
                    call.respond(
                        HttpStatusCode.NotFound,
                        Article.ErrorResponse("Blog not found")
                    )
                    return@put
                }

                if (blog.userId.value != userId) {
                    call.respond(
                        HttpStatusCode.Forbidden,
                        Article.ErrorResponse("Not authorized to edit this blog")
                    )
                    return@put
                }

                try {
                    val request = call.receive<Article.UpdateBlogRequest>()
                    val updatedBlog = blogService.updateBlog(blogId, request)
                    if (updatedBlog != null) {
                        call.respond(updatedBlog)
                    } else {
                        call.respond(
                            HttpStatusCode.InternalServerError,
                            Article.ErrorResponse("Failed to update blog")
                        )
                    }
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        Article.ErrorResponse("Failed to update blog: ${e.localizedMessage}")
                    )
                }
            }

            delete("/{id}") {
                val principal = call.principal<UserIdPrincipal>()
                val userId = principal?.name?.toIntOrNull()
                val blogId = call.parameters["id"]?.toIntOrNull()

                if (userId == null || blogId == null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        Article.ErrorResponse("Invalid request")
                    )
                    return@delete
                }

                val blog = blogService.getBlogById(blogId)
                if (blog == null) {
                    call.respond(
                        HttpStatusCode.NotFound,
                        Article.ErrorResponse("Blog not found")
                    )
                    return@delete
                }

                if (blog.userId.value != userId) {
                    call.respond(
                        HttpStatusCode.Forbidden,
                        Article.ErrorResponse("Not authorized to delete this blog")
                    )
                    return@delete
                }

                val deleted = blogService.deleteBlog(blogId)
                if (deleted) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        Article.ErrorResponse("Failed to delete blog")
                    )
                }
            }
        }
    }
}