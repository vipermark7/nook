package com.nookblog.views;

import com.nookblog.core.Blog;
import com.nookblog.core.Post;
import com.nookblog.core.User;
import io.dropwizard.views.View;
import java.util.List;

public class BlogView extends View {
    private final Blog blog;
    private final List<Post> posts;
    private final User owner;
    
    public BlogView(Blog blog, List<Post> posts, User owner) {
        super("blog.mustache");
        this.blog = blog;
        this.posts = posts;
        this.owner = owner;
    }
    
    public Blog getBlog() {
        return blog;
    }
    
    public List<Post> getPosts() {
        return posts;
    }
    
    public User getOwner() {
        return owner;
    }
}