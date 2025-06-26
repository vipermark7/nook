package com.nookblog.db;

import com.nookblog.core.Blog;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.List;
import java.util.ArrayList;

public class BlogDAO {
    private final ConcurrentHashMap<Long, Blog> blogs = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public Blog create(Long userId, String title, String description) {
        Blog blog = new Blog(userId, title, description);
        Long id = idCounter.getAndIncrement();
        blog.setId(id);
        blogs.put(id, blog);
        return blog;
    }

    public Blog findById(Long id) {
        return blogs.get(id);
    }

    public Blog findByUserId(Long userId) {
        return blogs.values().stream()
                .filter(blog -> blog.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    public List<Blog> findAll() {
        return new ArrayList<>(blogs.values());
    }

    public void update(Blog blog) {
        blogs.put(blog.getId(), blog);
    }

    public void delete(Long id) {
        blogs.remove(id);
    }
}