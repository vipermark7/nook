package com.nookblog.views;

import com.nookblog.core.Blog;
import com.nookblog.core.User;
import io.dropwizard.views.common.View;

public class ProfileView extends View {
    private final User user;
    private final Blog blog;
    
    public ProfileView(User user, Blog blog) {
        super("profile.mustache");
        this.user = user;
        this.blog = blog;
    }
    
    public User getUser() {
        return user;
    }
    
    public Blog getBlog() {
        return blog;
    }
}