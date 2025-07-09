package com.nookblog.services

import com.nookblog.models.Article
import com.nookblog.models.Blog
import com.nookblog.models.Blogs
import com.nookblog.models.blogDto
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class BlogService {
    fun createBlog(userId: Int, request: Article.CreateBlogRequest): Article.BlogDto = transaction {
        val blog = Blog.new {
            this.userId = userId
            title = request.title
            description = request.description
        }

        blog.blogDto()
    }

    fun getBlogByUserId(userId: Int): Article.BlogDto? = transaction {
        Blog.find { Blogs.userId eq userId }.firstOrNull()?.blogDto()
    }

    fun getBlogById(id: Int): Blog? = transaction {
        Blog.findById(id)
    }

    fun updateBlog(blogId: Int, request: Article.UpdateBlogRequest): Article.BlogDto? = transaction {
        val blog = Blog.findById(blogId) ?: return@transaction null

        request.title?.let { blog.title = it }
        request.description?.let { blog.description = it }
        blog.updatedAt = LocalDateTime.now()

        blog.blogDto()
    }

    fun deleteBlog(blogId: Int): Boolean = transaction {
        val blog = Blog.findById(blogId) ?: return@transaction false
        blog.delete()
        true
    }

    fun getAllBlogs(): List<Article.BlogDto> = transaction {
        Blog.all().map { it.blogDto() }
    }
}