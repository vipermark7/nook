package com.nookblog.resources;

import com.nookblog.db.BlogDAO;
import com.nookblog.db.PostDAO;
import com.nookblog.db.UserDAO;
import com.nookblog.views.BlogFormView;
import com.nookblog.views.BlogListView;
import com.nookblog.views.BlogView;
import com.nookblog.views.ErrorView;
import io.dropwizard.auth.Auth;
import io.dropwizard.views.common.View;
import javax.annotation.security.RolesAllowed;

import com.nookblog.core.Blog;
import com.nookblog.core.Post;
import com.nookblog.core.User;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.List;

@Path("/blogs")
@Produces(MediaType.TEXT_HTML)
public class BlogResource {
    
    private final BlogDAO blogDAO;
    private final PostDAO postDAO;
    private final UserDAO userDAO;
    
    public BlogResource(BlogDAO blogDAO, PostDAO postDAO, UserDAO userDAO) {
        this.blogDAO = blogDAO;
        this.postDAO = postDAO;
        this.userDAO = userDAO;
    }


    @GET
    public BlogListView listBlogs() {
        List<Blog> blogs = blogDAO.findAll();
        return new BlogListView(blogs, userDAO);
    }
    
    @GET
    @Path("/{id}")
    public BlogView getBlog(@PathParam("id") Long id) {
        Blog blog = blogDAO.findById(id);
        if (blog == null) {
            throw new WebApplicationException(404);
        }
        List<Post> posts = postDAO.findByBlogId(id);
        User owner = userDAO.findById(blog.getUserId());
        return new BlogView(blog, posts, owner);
    }
    
    @GET
    @Path("/new")
    @RolesAllowed("USER")
    public Object newBlogForm(@Auth User user) {
        Blog existingBlog = blogDAO.findByUserId(user.getId());
        if (existingBlog != null) {
            return new ErrorView("You already have a blog!");
        }
        return new BlogFormView(null);
    }
    
    @POST
    @RolesAllowed("USER")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response createBlog(@Auth User user,
                               @FormParam("title") String title,
                               @FormParam("description") String description) {
        Blog existingBlog = blogDAO.findByUserId(user.getId());
        if (existingBlog != null) {
            return Response.ok(new ErrorView("You already have a blog!")).build();
        }
        
        Blog blog = blogDAO.create(user.getId(), title, description);
        return Response.seeOther(URI.create("/blogs/" + blog.getId())).build();
    }
    
    @GET
    @Path("/{id}/edit")
    @RolesAllowed("USER")
    public BlogFormView editBlogForm(@Auth User user, @PathParam("id") Long id) {
        Blog blog = blogDAO.findById(id);
        if (blog == null) {
            throw new WebApplicationException(404);
        }
        if (!blog.getUserId().equals(user.getId())) {
            throw new WebApplicationException(403);
        }
        return new BlogFormView(blog);
    }
    
    @POST
    @Path("/{id}/edit")
    @RolesAllowed("USER")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response updateBlog(@Auth User user,
                              @PathParam("id") Long id,
                              @FormParam("title") String title,
                              @FormParam("description") String description) {
        Blog blog = blogDAO.findById(id);
        if (blog == null) {
            throw new WebApplicationException(404);
        }
        if (!blog.getUserId().equals(user.getId())) {
            throw new WebApplicationException(403);
        }
        
        blog.setTitle(title);
        blog.setDescription(description);
        blogDAO.update(blog);
        
        return Response.seeOther(URI.create("/blogs/" + id)).build();
    }
    
    @POST
    @Path("/{id}/delete")
    @RolesAllowed("USER")
    public Response deleteBlog(@Auth User user, @PathParam("id") Long id) {
        Blog blog = blogDAO.findById(id);
        if (blog == null) {
            throw new WebApplicationException(404);
        }
        if (!blog.getUserId().equals(user.getId())) {
            throw new WebApplicationException(403);
        }
        
        postDAO.deleteByBlogId(id);
        blogDAO.delete(id);
        
        return Response.seeOther(URI.create("/blogs")).build();
    }
}