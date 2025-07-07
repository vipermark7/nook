package com.nookblog.views;

import com.nookblog.core.Blog;
import io.dropwizard.views.common.View;

public class BlogFormView extends View {
    private final Blog blog;
    private final boolean isEdit;
    
    public BlogFormView(Blog blog) {
        super("blog-form.mustache");
        this.blog = blog;
        this.isEdit = blog != null;
    }
    
    public Blog getBlog() {
        return blog;
    }
    
    public boolean isEdit() {
        return isEdit;
    }
}