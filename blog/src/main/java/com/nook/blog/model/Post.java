package com.nook.blog.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "blogs", indexes = {
        @Index(name = "idx_blog_slug", columnList = "slug", unique = true),
        @Index(name = "idx_blog_author", columnList = "author_id"),
        @Index(name = "idx_blog_status_created", columnList = "status, created_at")
})
public class Post {

    public enum PostStatus {
        DRAFT,
        PUBLISHED,
        ARCHIVED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, unique = true, length = 255)
    private String slug;

    @Lob
    @Column(nullable = false)
    private String content;

    /**
     * Identifier of the author (e.g., user id from your auth domain).
     */
    @Column(name = "author_id", nullable = false, length = 100)
    private String authorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BlogStatus status = BlogStatus.DRAFT;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public Post() {
    }

    public Post(String title, String slug, String content, String authorId, BlogStatus status) {
        this.title = title;
        this.slug = slug;
        this.content = content;
        this.authorId = authorId;
        this.status = status != null ? status : BlogStatus.DRAFT;
    }

    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = BlogStatus.DRAFT;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Blog blog)) return false;
        return Objects.equals(id, blog.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Blog{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", slug='" + slug + '\'' +
                ", authorId='" + authorId + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
