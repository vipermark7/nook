package com.nook.blog.repositories;

import java.util.List;
import java.util.Optional;
import com.nook.blog.models.Blog;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogRepository extends JpaRepository<Blog, Long> {
    List<Blog> findByStatusOrderByCreatedAtDesc(Blog.BlogStatus status);
    Optional<Blog> findBySlug(String slug);
    List<Blog> findByAuthorIdOrderByCreatedAtDesc(String authorId);
}
