package com.nookblog.views;

import com.nookblog.core.Blog;
import com.nookblog.core.User;
import com.nookblog.db.UserDAO;
import io.dropwizard.views.common.View;
import java.util.List;
import java.util.ArrayList;

public class BlogListView extends View {
    private final List<BlogWithUser> blogsWithUsers;
    
    public BlogListView(List<Blog> blogs, UserDAO userDAO) {
        super("blog-list.mustache");
        this.blogsWithUsers = new ArrayList<>();
        for (Blog blog : blogs) {
            User user = userDAO.findById(blog.getUserId());
            blogsWithUsers.add(new BlogWithUser(blog, user));
        }
    }
    
    public List<BlogWithUser> getBlogsWithUsers() {
        return blogsWithUsers;
    }
    
    public static class BlogWithUser {
        private final Blog blog;
        private final User user;
        
        public BlogWithUser(Blog blog, User user) {
            this.blog = blog;
            this.user = user;
        }
        
        public Blog getBlog() {
            return blog;
        }
        
        public User getUser() {
            return user;
        }
    }
}