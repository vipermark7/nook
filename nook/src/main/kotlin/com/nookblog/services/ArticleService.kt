package com.nookblog.services

import com.nookblog.models.Article
import com.nookblog.models.Articles
import com.nookblog.models.articleDto
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class ArticleService {
    fun createArticle(blogId: Int, request: Article.CreateArticleRequest): Article.ArticleDto = transaction {
        val article = Article.new {
            this.blogId = blogId
            title = request.title
            content = request.content
            published = request.published
        }

        article.articleDto()
    }

    fun getArticlesByBlogId(blogId: Int): List<Article.ArticleDto> = transaction {
        Article.find { Articles.blogId eq blogId }.map { it.articleDto() }
    }

    fun getPublishedArticlesByBlogId(blogId: Int): List<Article.ArticleDto> = transaction {
        Article.find { (Articles.blogId eq blogId) and (Articles.published eq true) }
            .map { it.articleDto() }
    }

    fun getArticleById(id: Int): Article? = transaction {
        Article.findById(id)
    }

    fun updateArticle(articleId: Int, request: Article.UpdateArticleRequest): Article.ArticleDto? = transaction {
        val article = Article.findById(articleId) ?: return@transaction null

        request.title?.let { article.title = it }
        request.content?.let { article.content = it }
        request.published?.let { article.published = it }
        article.updatedAt = LocalDateTime.now()

        article.articleDto()
    }

    fun deleteArticle(articleId: Int): Boolean = transaction {
        val article = Article.findById(articleId) ?: return@transaction false
        article.delete()
        true
    }
}