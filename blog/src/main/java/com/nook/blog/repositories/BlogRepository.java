package com.nook.blog.repositories;

public interface BlogRepository extends JpaRepository<Blog, Long> {
    List<Blog> findByStatusOrderByCreatedAtDesc(Blog.BlogStatus status);
    Optional<Blog> findBySlug(String slug);
    List<Blog> findByAuthorIdOrderByCreatedAtDesc(String authorId);
}
