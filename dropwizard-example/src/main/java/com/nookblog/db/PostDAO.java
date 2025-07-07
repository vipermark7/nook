package com.nookblog.db;

import com.nookblog.core.Post;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

public class PostDAO {
    private final ConcurrentHashMap<Long, Post> posts = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public Post create(Long blogId, String title, String content) {
        Post post = new Post(blogId, title, content);
        Long id = idCounter.getAndIncrement();
        post.setId(id);
        posts.put(id, post);
        return post;
    }

    public Post findById(Long id) {
        return posts.get(id);
    }

    public List<Post> findByBlogId(Long blogId) {
        return posts.values().stream()
                .filter(post -> post.getBlogId().equals(blogId))
                .collect(Collectors.toList());
    }

    public List<Post> findAll() {
        return new ArrayList<>(posts.values());
    }

    public void update(Post post) {
        post.setUpdatedAt(LocalDateTime.now());
        posts.put(post.getId(), post);
    }

    public void delete(Long id) {
        posts.remove(id);
    }

    public void deleteByBlogId(Long blogId) {
        posts.entrySet().removeIf(entry -> entry.getValue().getBlogId().equals(blogId));
    }
}