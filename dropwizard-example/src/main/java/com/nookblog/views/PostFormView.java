package com.nookblog.views;

import com.nookblog.core.Post;
import io.dropwizard.views.common.View;


public class PostFormView extends View {
    private final Post post;
    private final Long blogId;
    private final boolean isEdit;
    
    public PostFormView(Post post, Long blogId) {
        super("post-form.mustache");
        this.post = post;
        this.blogId = blogId;
        this.isEdit = post != null;
    }
    
    public Post getPost() {
        return post;
    }
}