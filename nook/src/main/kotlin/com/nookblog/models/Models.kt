package com.nookblog.models;

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

// Database Tables
object Users : IntIdTable() {
    val username = varchar("username", 50).uniqueIndex()
    val email = varchar("email", 100).uniqueIndex()
    val passwordHash = varchar("password_hash", 100)
    val createdAt = datetime("created_at").default(LocalDateTime.now())
}

object Blogs : IntIdTable() {
    val userId = reference("user_id", Users).uniqueIndex()
    val title = varchar("title", 200)
    val description = text("description").nullable()
    val createdAt = datetime("created_at").default(LocalDateTime.now())
    val updatedAt = datetime("updated_at").default(LocalDateTime.now())
}

object Articles : IntIdTable() {
    val blogId = reference("blog_id", Blogs)
    val title = varchar("title", 200)
    val content = text("content")
    val published = bool("published").default(false)
    val createdAt = datetime("created_at").default(LocalDateTime.now())
    val updatedAt = datetime("updated_at").default(LocalDateTime.now())
}

// DAO Entities
class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users)

    var username by Users.username
    var email by Users.email
    var passwordHash by Users.passwordHash
    var createdAt by Users.createdAt

    val blog by Blog optionalBackReferencedOn Blogs.userId
}

class Blog(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Blog>(Blogs)

    var userId by Blogs.userId
    var title by Blogs.title
    var description by Blogs.description
    var createdAt by Blogs.createdAt
    var updatedAt by Blogs.updatedAt

    var user by User referencedOn Blogs.userId
    val articles by Article referrersOn Articles.blogId
}

class Article(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Article>(Articles)

    var blogId by Articles.blogId
    var title by Articles.title
    var content by Articles.content
    var published by Articles.published
    var createdAt by Articles.createdAt
    var updatedAt by Articles.updatedAt

    var blog by Blog referencedOn Articles.blogId
    // DTOs
    @Serializable
    data class UserDto(
        val id: Int,
        val username: String,
        val email: String,
        val createdAt: String
    )

    @Serializable
    data class CreateUserRequest(
        val username: String,
        val email: String,
        val password: String
    )

    @Serializable
    data class LoginRequest(
        val username: String,
        val password: String
    )

    @Serializable
    data class BlogDto(
        val id: Int,
        val userId: Int,
        val title: String,
        val description: String?,
        val createdAt: String,
        val updatedAt: String
    )

    @Serializable
    data class CreateBlogRequest(
        val title: String,
        val description: String?
    )

    @Serializable
    data class UpdateBlogRequest(
        val title: String?,
        val description: String?
    )

    @Serializable
    data class ArticleDto(
        val id: Int,
        val blogId: Int,
        val title: String,
        val content: String,
        val published: Boolean,
        val createdAt: String,
        val updatedAt: String
    )

    @Serializable
    data class CreateArticleRequest(
        val title: String,
        val content: String,
        val published: Boolean = false
    )

    @Serializable
    data class UpdateArticleRequest(
        val title: String?,
        val content: String?,
        val published: Boolean?
    )

    @Serializable
    data class AuthResponse(
        val user: UserDto
    )

    @Serializable
    data class ErrorResponse(
        val message: String
    )

}

// Extension functions for converting entities to DTOs
fun User.userDto() = Article.UserDto(
    id = id.value,
    username = username,
    email = email,
    createdAt = createdAt.toString()
)

fun Blog.blogDto() = Article.BlogDto(
    id = id.value,
    userId = userId.value,
    title = title,
    description = description,
    createdAt = createdAt.toString(),
    updatedAt = updatedAt.toString()
)

fun Article.articleDto() = Article.ArticleDto(
    id = id.value,
    blogId = blogId.value,
    title = title,
    content = content,
    published = published,
    createdAt = createdAt.toString(),
    updatedAt = updatedAt.toString()
)