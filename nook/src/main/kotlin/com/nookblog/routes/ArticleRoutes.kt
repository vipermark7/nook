package com.nookblog.routes

import com.nookblog.com.nookblog.services.BlogService
import com.nookblog.models.*
import com.nookblog.services.ArticleService
import com.nookblog.models.Article
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.articleRoutes() {
    val articleService = ArticleService()
    val blogService = BlogService()

    route("/articles") {
        get("/blog/{blogId}") {
            val blogId = call.parameters["blogId"]?.toIntOrNull()
            if (blogId == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    Article.ErrorResponse("Invalid blog ID")
                )
                return@get
            }

            val articles = articleService.getPublishedArticlesByBlogId(blogId)
            call.respond(articles)
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    Article.ErrorResponse("Invalid article ID")
                )
                return@get
            }

            val article = articleService.getArticleById(id)
            if (article != null && article.published) {
                call.respond(article.articleDto())
            } else {
                call.respond(
                    HttpStatusCode.NotFound,
                    Article.ErrorResponse("Article not found")
                )
            }
        }

        authenticate("auth-basic") {
            post("/blog/{blogId}") {
                val principal = call.principal<UserIdPrincipal>()
                val userId = principal?.name?.toIntOrNull()
                val blogId = call.parameters["blogId"]?.toIntOrNull()

                if (userId == null || blogId == null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        Article.ErrorResponse("Invalid request")
                    )
                    return@post
                }

                val blog = blogService.getBlogById(blogId)
                if (blog == null) {
                    call.respond(
                        HttpStatusCode.NotFound,
                        Article.ErrorResponse("Blog not found")
                    )
                    return@post
                }

                if (blog.userId.value != userId) {
                    call.respond(
                        HttpStatusCode.Forbidden,
                        Article.ErrorResponse("Not authorized to create articles in this blog")
                    )
                    return@post
                }

                try {
                    val request = call.receive<Article.CreateArticleRequest>()
                    val article = articleService.createArticle(blogId, request)
                    call.respond(HttpStatusCode.Created, article)
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        Article.ErrorResponse("Failed to create article: ${e.localizedMessage}")
                    )
                }
            }

            get("/my/blog/{blogId}") {
                val principal = call.principal<UserIdPrincipal>()
                val userId = principal?.name?.toIntOrNull()
                val blogId = call.parameters["blogId"]?.toIntOrNull()

                if (userId == null || blogId == null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        Article.ErrorResponse("Invalid request")
                    )
                    return@get
                }

                val blog = blogService.getBlogById(blogId)
                if (blog == null) {
                    call.respond(
                        HttpStatusCode.NotFound,
                        Article.ErrorResponse("Blog not found")
                    )
                    return@get
                }

                if (blog.userId.value != userId) {
                    call.respond(
                        HttpStatusCode.Forbidden,
                        Article.ErrorResponse("Not authorized to view this blog's articles")
                    )
                    return@get
                }

                val articles = articleService.getArticlesByBlogId(blogId)
                call.respond(articles)
            }

            put("/{id}") {
                val principal = call.principal<UserIdPrincipal>()
                val userId = principal?.name?.toIntOrNull()
                val articleId = call.parameters["id"]?.toIntOrNull()

                if (userId == null || articleId == null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        Article.ErrorResponse("Invalid request")
                    )
                    return@put
                }

                val article = articleService.getArticleById(articleId)
                if (article == null) {
                    call.respond(
                        HttpStatusCode.NotFound,
                        Article.ErrorResponse("Article not found")
                    )
                    return@put
                }

                val blog = blogService.getBlogById(article.blogId.value)
                if (blog == null || blog.userId.value != userId) {
                    call.respond(
                        HttpStatusCode.Forbidden,
                        Article.ErrorResponse("Not authorized to edit this article")
                    )
                    return@put
                }

                try {
                    val request = call.receive<Article.UpdateArticleRequest>()
                    val updatedArticle = articleService.updateArticle(articleId, request)
                    if (updatedArticle != null) {
                        call.respond(updatedArticle)
                    } else {
                        call.respond(
                            HttpStatusCode.InternalServerError,
                            Article.ErrorResponse("Failed to update article")
                        )
                    }
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        Article.ErrorResponse("Failed to update article: ${e.localizedMessage}")
                    )
                }
            }

            delete("/{id}") {
                val principal = call.principal<UserIdPrincipal>()
                val userId = principal?.name?.toIntOrNull()
                val articleId = call.parameters["id"]?.toIntOrNull()

                if (userId == null || articleId == null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        Article.ErrorResponse("Invalid request")
                    )
                    return@delete
                }

                val article = articleService.getArticleById(articleId)
                if (article == null) {
                    call.respond(
                        HttpStatusCode.NotFound,
                        Article.ErrorResponse("Article not found")
                    )
                    return@delete
                }

                val blog = blogService.getBlogById(article.blogId.value)
                if (blog == null || blog.userId.value != userId) {
                    call.respond(
                        HttpStatusCode.Forbidden,
                        Article.ErrorResponse("Not authorized to delete this article")
                    )
                    return@delete
                }

                val deleted = articleService.deleteArticle(articleId)
                if (deleted) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        Article.ErrorResponse("Failed to delete article")
                    )
                }
            }
        }
    }
}